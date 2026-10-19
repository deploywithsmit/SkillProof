package com.skillproof.profile;

import com.skillproof.common.ForbiddenException;
import com.skillproof.common.ResourceNotFoundException;
import com.skillproof.review.ReviewService;
import com.skillproof.review.SubmissionReview;
import com.skillproof.review.SubmissionReviewRepository;
import com.skillproof.review.VerifiedSkillDto;
import com.skillproof.security.SecurityUtils;
import com.skillproof.security.UserPrincipal;
import com.skillproof.submission.Submission;
import com.skillproof.submission.SubmissionRepository;
import com.skillproof.submission.SubmissionResponseDto;
import com.skillproof.submission.SubmissionService;
import com.skillproof.submission.SubmissionStatus;
import com.skillproof.user.User;
import com.skillproof.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final ProfileSettingsRepository profileSettingsRepository;
    private final ReviewService reviewService;
    private final SubmissionService submissionService;
    private final SubmissionRepository submissionRepository;
    private final SubmissionReviewRepository reviewRepository;

    public ProfileService(UserRepository userRepository,
                          ProfileSettingsRepository profileSettingsRepository,
                          ReviewService reviewService,
                          SubmissionService submissionService,
                          SubmissionRepository submissionRepository,
                          SubmissionReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.profileSettingsRepository = profileSettingsRepository;
        this.reviewService = reviewService;
        this.submissionService = submissionService;
        this.submissionRepository = submissionRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    public ProfileDto getPublicProfile(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for username: " + username));

        ProfileSettings settings = profileSettingsRepository.findByUserId(user.getId())
                .orElseGet(() -> new ProfileSettings(user));

        Long currentUserId = SecurityUtils.getCurrentUserPrincipal().map(UserPrincipal::getId).orElse(null);
        boolean isSelf = currentUserId != null && currentUserId.equals(user.getId());

        if (!settings.isPublic() && !isSelf) {
            throw new ForbiddenException("This profile has been marked private by the user.");
        }

        ProfileDto dto = new ProfileDto();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setRole(user.getRole().name());
        dto.setHeadline(settings.getHeadline());
        dto.setBio(settings.getBio());
        dto.setTechnicalInterests(settings.getTechnicalInterests());
        dto.setGithubUrl(settings.getGithubUrl());
        dto.setLinkedinUrl(settings.getLinkedinUrl());
        dto.setPublic(settings.isPublic());
        dto.setShowOnLeaderboard(settings.isShowOnLeaderboard());
        dto.setShowReviews(settings.isShowReviews());

        // Verified Skills
        List<VerifiedSkillDto> verifiedSkills = reviewService.getVerifiedSkillsForUser(user.getId());
        dto.setVerifiedSkills(verifiedSkills);

        // Approved Projects
        List<Submission> approvedSubs = submissionRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .filter(s -> s.getStatus() == SubmissionStatus.APPROVED)
                .toList();

        List<SubmissionResponseDto> approvedDtos = approvedSubs.stream()
                .map(submissionService::mapToDto)
                .toList();
        dto.setApprovedProjects(approvedDtos);

        // Mentor reviews if allowed
        if (settings.isShowReviews() || isSelf) {
            List<ProfileDto.PublicReviewDto> publicReviews = approvedSubs.stream()
                    .map(sub -> reviewRepository.findBySubmissionId(sub.getId()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(SubmissionReview::isPublic)
                    .map(rev -> new ProfileDto.PublicReviewDto(
                            rev.getSubmission().getChallenge().getTitle(),
                            rev.getReviewer().getFullName(),
                            rev.getTotalScore(),
                            rev.getGeneralFeedback()
                    ))
                    .toList();
            dto.setMentorReviews(publicReviews);
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public ProfileDto getMyProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return getPublicProfile(user.getUsername());
    }

    @Transactional
    public ProfileDto updateMyProfile(UpdateProfileRequestDto request) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
            userRepository.save(user);
        }

        ProfileSettings settings = profileSettingsRepository.findByUserId(userId)
                .orElseGet(() -> new ProfileSettings(user));

        if (request.getHeadline() != null) settings.setHeadline(request.getHeadline().trim());
        if (request.getBio() != null) settings.setBio(request.getBio().trim());
        if (request.getTechnicalInterests() != null) settings.setTechnicalInterests(request.getTechnicalInterests().trim());
        if (request.getGithubUrl() != null) settings.setGithubUrl(request.getGithubUrl().trim());
        if (request.getLinkedinUrl() != null) settings.setLinkedinUrl(request.getLinkedinUrl().trim());
        if (request.getPublic() != null) settings.setPublic(request.getPublic());
        if (request.getShowOnLeaderboard() != null) settings.setShowOnLeaderboard(request.getShowOnLeaderboard());
        if (request.getShowReviews() != null) settings.setShowReviews(request.getShowReviews());

        profileSettingsRepository.save(settings);
        return getPublicProfile(user.getUsername());
    }
}
