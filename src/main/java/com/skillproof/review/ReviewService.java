package com.skillproof.review;

import com.skillproof.common.BadRequestException;
import com.skillproof.common.ForbiddenException;
import com.skillproof.common.ResourceNotFoundException;
import com.skillproof.notification.NotificationService;
import com.skillproof.security.SecurityUtils;
import com.skillproof.security.UserPrincipal;
import com.skillproof.skill.Skill;
import com.skillproof.skill.UserSkill;
import com.skillproof.skill.UserSkillRepository;
import com.skillproof.skill.UserSkillStatus;
import com.skillproof.submission.Submission;
import com.skillproof.submission.SubmissionRepository;
import com.skillproof.submission.SubmissionResponseDto;
import com.skillproof.submission.SubmissionStatus;
import com.skillproof.user.ActivityService;
import com.skillproof.user.Role;
import com.skillproof.user.User;
import com.skillproof.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    private final SubmissionReviewRepository reviewRepository;
    private final ReviewScoreRepository reviewScoreRepository;
    private final VerifiedSkillRepository verifiedSkillRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;
    private final NotificationService notificationService;
    private final ActivityService activityService;

    public ReviewService(SubmissionReviewRepository reviewRepository,
                         ReviewScoreRepository reviewScoreRepository,
                         VerifiedSkillRepository verifiedSkillRepository,
                         SubmissionRepository submissionRepository,
                         UserRepository userRepository,
                         UserSkillRepository userSkillRepository,
                         NotificationService notificationService,
                         ActivityService activityService) {
        this.reviewRepository = reviewRepository;
        this.reviewScoreRepository = reviewScoreRepository;
        this.verifiedSkillRepository = verifiedSkillRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.userSkillRepository = userSkillRepository;
        this.notificationService = notificationService;
        this.activityService = activityService;
    }

    @Transactional(readOnly = true)
    public Page<Submission> getMentorQueue(SubmissionStatus status, Pageable pageable) {
        UserPrincipal principal = SecurityUtils.getRequiredCurrentUserPrincipal();
        Long mentorId = principal.getRole() == Role.ADMIN ? null : principal.getId();
        return submissionRepository.searchMentorSubmissions(status, mentorId, pageable);
    }

    @Transactional
    public SubmissionReview submitReview(Long submissionId, ReviewRequestDto request) {
        UserPrincipal principal = SecurityUtils.getRequiredCurrentUserPrincipal();
        if (principal.getRole() != Role.MENTOR && principal.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Only mentors and administrators can review submissions.");
        }

        User reviewer = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer user record not found"));

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with ID: " + submissionId));

        if (submission.getStatus() != SubmissionStatus.SUBMITTED &&
            submission.getStatus() != SubmissionStatus.UNDER_REVIEW &&
            submission.getStatus() != SubmissionStatus.CHANGES_REQUESTED) {
            throw new BadRequestException("This submission cannot be reviewed in its current state: " + submission.getStatus());
        }

        // Create or update review
        SubmissionReview review = reviewRepository.findBySubmissionId(submissionId)
                .orElseGet(() -> new SubmissionReview());

        review.setSubmission(submission);
        review.setReviewer(reviewer);
        review.setDecision(request.getDecision());
        review.setTotalScore(request.getTotalScore());
        review.setGeneralFeedback(request.getGeneralFeedback().trim());
        review.setImprovementSuggestions(request.getImprovementSuggestions() != null ? request.getImprovementSuggestions().trim() : null);
        review.setPublic(request.isPublic());
        review.setReviewedAt(Instant.now());

        review = reviewRepository.save(review);

        // Save rubric scores
        if (request.getScores() != null && !request.getScores().isEmpty()) {
            for (ReviewRequestDto.CriterionScoreItem item : request.getScores()) {
                ReviewScore score = new ReviewScore(
                        review,
                        item.getCriterionName(),
                        item.getScore(),
                        item.getMaxScore(),
                        item.getComments()
                );
                reviewScoreRepository.save(score);
            }
        }

        User student = submission.getUser();
        Skill skill = submission.getChallenge().getSkill();

        if (request.getDecision() == ReviewDecision.APPROVED) {
            submission.setStatus(SubmissionStatus.APPROVED);

            // Update user skill progression to 100% and VERIFIED
            UserSkill userSkill = userSkillRepository.findByUserIdAndSkillId(student.getId(), skill.getId())
                    .orElseGet(() -> new UserSkill(student, skill, UserSkillStatus.VERIFIED, 100));

            userSkill.setStatus(UserSkillStatus.VERIFIED);
            userSkill.setProgressPercent(100);
            userSkill.setCompletedAt(Instant.now());
            userSkillRepository.save(userSkill);

            // Create VerifiedSkill record if not already verified
            if (!verifiedSkillRepository.existsByUserIdAndSkillId(student.getId(), skill.getId())) {
                String badgeCode = "VP-" + skill.getSlug().toUpperCase().replace("-", "")
                        + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

                VerifiedSkill verifiedSkill = new VerifiedSkill(student, skill, submission, reviewer, badgeCode);
                verifiedSkillRepository.save(verifiedSkill);
            }

            notificationService.notifyUser(
                    student,
                    "Skill Verified: " + skill.getName(),
                    "Congratulations! Your work on '" + submission.getChallenge().getTitle() + "' was approved by " + reviewer.getFullName() + " with score " + request.getTotalScore() + ".",
                    "SKILL_VERIFIED",
                    "/verified.html"
            );

            activityService.logActivity(
                    student,
                    "SKILL_VERIFIED",
                    "Earned verified skill badge for " + skill.getName() + " (Score: " + request.getTotalScore() + ")",
                    submission.getId()
            );

        } else if (request.getDecision() == ReviewDecision.CHANGES_REQUESTED) {
            submission.setStatus(SubmissionStatus.CHANGES_REQUESTED);

            notificationService.notifyUser(
                    student,
                    "Changes Requested on " + submission.getChallenge().getTitle(),
                    "Mentor " + reviewer.getFullName() + " has reviewed your submission and requested adjustments. Check the feedback to revise.",
                    "CHANGES_REQUESTED",
                    "/submissions.html"
            );

            activityService.logActivity(
                    student,
                    "CHANGES_REQUESTED",
                    "Mentor requested revisions on " + submission.getChallenge().getTitle(),
                    submission.getId()
            );

        } else {
            submission.setStatus(SubmissionStatus.REJECTED);

            notificationService.notifyUser(
                    student,
                    "Submission Evaluated",
                    "Your submission for '" + submission.getChallenge().getTitle() + "' was reviewed by " + reviewer.getFullName() + ".",
                    "REVIEW_COMPLETED",
                    "/submissions.html"
            );
        }

        submissionRepository.save(submission);
        logger.info("Mentor {} submitted review for submission {} with decision: {}", reviewer.getEmail(), submission.getId(), request.getDecision());
        return review;
    }

    @Transactional(readOnly = true)
    public List<VerifiedSkillDto> getVerifiedSkillsForUser(Long userId) {
        List<VerifiedSkill> list = verifiedSkillRepository.findByUserIdOrderByVerifiedAtDesc(userId);
        return list.stream().map(vs -> {
            String challengeTitle = vs.getVerifiedBySubmission().getChallenge().getTitle();
            Integer score = reviewRepository.findBySubmissionId(vs.getVerifiedBySubmission().getId())
                    .map(SubmissionReview::getTotalScore)
                    .orElse(null);
            return new VerifiedSkillDto(vs, challengeTitle, score);
        }).toList();
    }

    @Transactional(readOnly = true)
    public VerifiedSkillDto getVerifiedSkillByBadgeCode(String badgeCode) {
        VerifiedSkill vs = verifiedSkillRepository.findByVerificationBadgeCode(badgeCode)
                .orElseThrow(() -> new ResourceNotFoundException("No verified skill found with badge code: " + badgeCode));

        String challengeTitle = vs.getVerifiedBySubmission().getChallenge().getTitle();
        Integer score = reviewRepository.findBySubmissionId(vs.getVerifiedBySubmission().getId())
                .map(SubmissionReview::getTotalScore)
                .orElse(null);
        return new VerifiedSkillDto(vs, challengeTitle, score);
    }
}
