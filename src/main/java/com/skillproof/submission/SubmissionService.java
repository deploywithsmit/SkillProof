package com.skillproof.submission;

import com.skillproof.challenge.Challenge;
import com.skillproof.challenge.ChallengePrerequisiteRepository;
import com.skillproof.challenge.ChallengeRepository;
import com.skillproof.common.BadRequestException;
import com.skillproof.common.ForbiddenException;
import com.skillproof.common.ResourceNotFoundException;
import com.skillproof.notification.NotificationService;
import com.skillproof.review.ReviewScoreRepository;
import com.skillproof.review.SubmissionReview;
import com.skillproof.review.SubmissionReviewRepository;
import com.skillproof.security.SecurityUtils;
import com.skillproof.security.UserPrincipal;
import com.skillproof.skill.Skill;
import com.skillproof.skill.UserSkillRepository;
import com.skillproof.skill.UserSkillStatus;
import com.skillproof.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.*;

@Service
public class SubmissionService {

    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);

    private final SubmissionRepository submissionRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengePrerequisiteRepository challengePrerequisiteRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;
    private final SubmissionReviewRepository reviewRepository;
    private final ReviewScoreRepository reviewScoreRepository;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;
    private final ActivityService activityService;

    public SubmissionService(SubmissionRepository submissionRepository,
                             ChallengeRepository challengeRepository,
                             ChallengePrerequisiteRepository challengePrerequisiteRepository,
                             UserSkillRepository userSkillRepository,
                             UserRepository userRepository,
                             SubmissionReviewRepository reviewRepository,
                             ReviewScoreRepository reviewScoreRepository,
                             FileStorageService fileStorageService,
                             NotificationService notificationService,
                             ActivityService activityService) {
        this.submissionRepository = submissionRepository;
        this.challengeRepository = challengeRepository;
        this.challengePrerequisiteRepository = challengePrerequisiteRepository;
        this.userSkillRepository = userSkillRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.reviewScoreRepository = reviewScoreRepository;
        this.fileStorageService = fileStorageService;
        this.notificationService = notificationService;
        this.activityService = activityService;
    }

    @Transactional
    public SubmissionResponseDto createOrUpdateSubmission(Long challengeId, SubmissionRequestDto request, MultipartFile file) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with ID: " + challengeId));

        // Check prerequisites
        List<Skill> prereqs = challengePrerequisiteRepository.findPrerequisiteSkillsForChallenge(challengeId);
        if (!prereqs.isEmpty()) {
            Set<Long> completedSkills = new HashSet<>();
            userSkillRepository.findByUserId(userId).forEach(us -> {
                if (us.getStatus() == UserSkillStatus.COMPLETED || us.getStatus() == UserSkillStatus.VERIFIED) {
                    completedSkills.add(us.getSkill().getId());
                }
            });
            boolean eligible = prereqs.stream().allMatch(p -> completedSkills.contains(p.getId()));
            if (!eligible) {
                throw new BadRequestException("You are not eligible for this challenge yet. Please complete all prerequisite skills first.");
            }
        }

        // Validate URLs if provided
        if (request.getRepoUrl() != null && !request.getRepoUrl().isBlank()) {
            validateUrl(request.getRepoUrl().trim(), "Repository URL");
        }
        if (request.getProjectUrl() != null && !request.getProjectUrl().isBlank()) {
            validateUrl(request.getProjectUrl().trim(), "Project URL");
        }

        // Check existing submission
        Optional<Submission> existingOpt = submissionRepository.findTopByUserIdAndChallengeIdOrderByCreatedAtDesc(userId, challengeId);
        Submission submission;

        if (existingOpt.isPresent()) {
            Submission existing = existingOpt.get();
            if (existing.getStatus() == SubmissionStatus.APPROVED) {
                throw new BadRequestException("You have already completed and had this challenge approved.");
            }
            if (existing.getStatus() == SubmissionStatus.SUBMITTED || existing.getStatus() == SubmissionStatus.UNDER_REVIEW) {
                throw new BadRequestException("You already have an active submission under review for this challenge.");
            }
            submission = existing;
        } else {
            submission = new Submission(challenge, user);
        }

        submission.setRepoUrl(request.getRepoUrl() != null ? request.getRepoUrl().trim() : null);
        submission.setProjectUrl(request.getProjectUrl() != null ? request.getProjectUrl().trim() : null);
        submission.setExplanation(request.getExplanation() != null ? request.getExplanation().trim() : null);

        // Store file if attached
        if (file != null && !file.isEmpty()) {
            FileStorageService.StoredFile storedFile = fileStorageService.store(file);
            submission.setAttachmentPath(storedFile.storedPath());
            submission.setAttachmentFilename(storedFile.originalFilename());
            submission.setAttachmentContentType(storedFile.contentType());
            submission.setAttachmentSizeBytes(storedFile.sizeBytes());
        }

        if (request.isDraft()) {
            submission.setStatus(SubmissionStatus.DRAFT);
            submission = submissionRepository.save(submission);
            return mapToDto(submission);
        }

        // Actual submission requires at least one form of evidence
        boolean hasRepo = submission.getRepoUrl() != null && !submission.getRepoUrl().isBlank();
        boolean hasProject = submission.getProjectUrl() != null && !submission.getProjectUrl().isBlank();
        boolean hasFile = submission.getAttachmentPath() != null;
        boolean hasText = submission.getExplanation() != null && !submission.getExplanation().isBlank();

        if (!hasRepo && !hasProject && !hasFile && !hasText) {
            throw new BadRequestException("Please provide a repository URL, project link, supporting text, or uploaded archive as proof.");
        }

        submission.setStatus(SubmissionStatus.SUBMITTED);
        submission.setSubmittedAt(Instant.now());

        // Assign to an available mentor
        List<User> mentors = userRepository.findByRole(Role.MENTOR);
        if (!mentors.isEmpty()) {
            // Simple deterministic round-robin / selection
            User selectedMentor = mentors.get((int) (submission.getId() != null ? submission.getId() % mentors.size() : 0));
            submission.setAssignedMentor(selectedMentor);
            submission.setStatus(SubmissionStatus.UNDER_REVIEW);
        }

        submission = submissionRepository.save(submission);

        // Log activity and create notification
        activityService.logActivity(user, "CHALLENGE_SUBMITTED", "Submitted evidence for " + challenge.getTitle(), submission.getId());
        notificationService.notifyUser(
                user,
                "Submission Received",
                "Your submission for '" + challenge.getTitle() + "' has been received and queued for mentor review.",
                "SUBMISSION_RECEIVED",
                "/submissions.html"
        );

        logger.info("User {} submitted evidence for challenge {} (ID: {})", user.getEmail(), challenge.getTitle(), submission.getId());
        return mapToDto(submission);
    }

    @Transactional(readOnly = true)
    public Page<SubmissionResponseDto> getMySubmissions(Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserId();
        return submissionRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public SubmissionResponseDto getSubmissionDetails(Long submissionId) {
        UserPrincipal principal = SecurityUtils.getRequiredCurrentUserPrincipal();
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with ID: " + submissionId));

        boolean isOwner = submission.getUser().getId().equals(principal.getId());
        boolean isStaff = principal.getRole() == Role.MENTOR || principal.getRole() == Role.ADMIN;

        if (!isOwner && !isStaff) {
            throw new ForbiddenException("You are not authorized to view this submission.");
        }

        return mapToDto(submission);
    }

    public Resource loadAttachment(Long submissionId) {
        UserPrincipal principal = SecurityUtils.getRequiredCurrentUserPrincipal();
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with ID: " + submissionId));

        boolean isOwner = submission.getUser().getId().equals(principal.getId());
        boolean isStaff = principal.getRole() == Role.MENTOR || principal.getRole() == Role.ADMIN;

        if (!isOwner && !isStaff) {
            throw new ForbiddenException("You are not authorized to access this file.");
        }

        if (submission.getAttachmentPath() == null) {
            throw new ResourceNotFoundException("No attachment associated with this submission.");
        }

        return fileStorageService.loadAsResource(submission.getAttachmentPath());
    }

    public SubmissionResponseDto mapToDto(Submission sub) {
        SubmissionResponseDto dto = new SubmissionResponseDto();
        dto.setId(sub.getId());
        dto.setChallengeId(sub.getChallenge().getId());
        dto.setChallengeTitle(sub.getChallenge().getTitle());
        dto.setChallengeSlug(sub.getChallenge().getSlug());
        dto.setSkillId(sub.getChallenge().getSkill().getId());
        dto.setSkillName(sub.getChallenge().getSkill().getName());
        dto.setStudentId(sub.getUser().getId());
        dto.setStudentName(sub.getUser().getFullName());
        dto.setStudentUsername(sub.getUser().getUsername());
        dto.setStatus(sub.getStatus());
        dto.setRepoUrl(sub.getRepoUrl());
        dto.setProjectUrl(sub.getProjectUrl());
        dto.setExplanation(sub.getExplanation());
        dto.setAttachmentFilename(sub.getAttachmentFilename());
        dto.setAttachmentSizeBytes(sub.getAttachmentSizeBytes());
        if (sub.getAssignedMentor() != null) {
            dto.setAssignedMentorId(sub.getAssignedMentor().getId());
            dto.setAssignedMentorName(sub.getAssignedMentor().getFullName());
        }
        dto.setSubmittedAt(sub.getSubmittedAt());
        dto.setCreatedAt(sub.getCreatedAt());

        // Attach review if exists
        reviewRepository.findBySubmissionId(sub.getId()).ifPresent(rev -> {
            dto.setReviewId(rev.getId());
            dto.setReviewDecision(rev.getDecision());
            dto.setTotalScore(rev.getTotalScore());
            dto.setGeneralFeedback(rev.getGeneralFeedback());
            dto.setImprovementSuggestions(rev.getImprovementSuggestions());
            dto.setReviewerName(rev.getReviewer().getFullName());
            dto.setReviewedAt(rev.getReviewedAt());

            List<SubmissionResponseDto.ReviewScoreDto> scores = reviewScoreRepository.findByReviewId(rev.getId())
                    .stream()
                    .map(s -> new SubmissionResponseDto.ReviewScoreDto(s.getCriterionName(), s.getScore(), s.getMaxScore(), s.getComments()))
                    .toList();
            dto.setRubricScores(scores);
        });

        return dto;
    }

    private void validateUrl(String url, String fieldName) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new BadRequestException(fieldName + " must be a valid URL starting with http:// or https://");
        }
    }
}
