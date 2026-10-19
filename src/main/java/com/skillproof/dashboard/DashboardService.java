package com.skillproof.dashboard;

import com.skillproof.common.ResourceNotFoundException;
import com.skillproof.review.VerifiedSkillRepository;
import com.skillproof.security.SecurityUtils;
import com.skillproof.skill.*;
import com.skillproof.submission.Submission;
import com.skillproof.submission.SubmissionRepository;
import com.skillproof.submission.SubmissionResponseDto;
import com.skillproof.submission.SubmissionService;
import com.skillproof.submission.SubmissionStatus;
import com.skillproof.user.ActivityService;
import com.skillproof.user.User;
import com.skillproof.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final UserSkillRepository userSkillRepository;
    private final SubmissionRepository submissionRepository;
    private final VerifiedSkillRepository verifiedSkillRepository;
    private final SkillService skillService;
    private final SubmissionService submissionService;
    private final ActivityService activityService;
    private final SkillPrerequisiteRepository prerequisiteRepository;

    public DashboardService(UserRepository userRepository,
                            UserSkillRepository userSkillRepository,
                            SubmissionRepository submissionRepository,
                            VerifiedSkillRepository verifiedSkillRepository,
                            SkillService skillService,
                            SubmissionService submissionService,
                            ActivityService activityService,
                            SkillPrerequisiteRepository prerequisiteRepository) {
        this.userRepository = userRepository;
        this.userSkillRepository = userSkillRepository;
        this.submissionRepository = submissionRepository;
        this.verifiedSkillRepository = verifiedSkillRepository;
        this.skillService = skillService;
        this.submissionService = submissionService;
        this.activityService = activityService;
        this.prerequisiteRepository = prerequisiteRepository;
    }

    @Transactional(readOnly = true)
    public DashboardOverviewDto getOverview() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        DashboardOverviewDto dto = new DashboardOverviewDto();
        dto.setWelcomeName(user.getFullName());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole().name());

        // Real Database Metrics
        List<UserSkill> userSkills = userSkillRepository.findByUserId(userId);
        dto.setSkillsPursuedCount(userSkills.size());

        List<Submission> allSubs = submissionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        Set<Long> approvedChallengeIds = new HashSet<>();
        int basePoints = 0;
        long pendingCount = 0;

        for (Submission s : allSubs) {
            if (s.getStatus() == SubmissionStatus.APPROVED) {
                if (approvedChallengeIds.add(s.getChallenge().getId())) {
                    basePoints += s.getChallenge().getPoints();
                }
            } else if (s.getStatus() == SubmissionStatus.SUBMITTED || s.getStatus() == SubmissionStatus.UNDER_REVIEW) {
                pendingCount++;
            }
        }

        long verifiedCount = verifiedSkillRepository.countByUserId(userId);
        dto.setChallengesCompletedCount(approvedChallengeIds.size());
        dto.setPendingSubmissionsCount(pendingCount);
        dto.setVerifiedSkillsCount(verifiedCount);
        dto.setTotalPoints(basePoints + (int) (verifiedCount * 50));

        // In-progress skills
        List<SkillSummaryDto> inProgress = userSkills.stream()
                .map(us -> {
                    int prereqCount = prerequisiteRepository.findBySkillId(us.getSkill().getId()).size();
                    return new SkillSummaryDto(us.getSkill(), us, true, prereqCount);
                })
                .toList();
        dto.setInProgressSkills(inProgress);

        // Recommendations
        dto.setRecommendations(skillService.getRecommendations());

        // Recent feedback from reviews
        List<SubmissionResponseDto> feedbackList = allSubs.stream()
                .filter(s -> s.getStatus() == SubmissionStatus.APPROVED ||
                             s.getStatus() == SubmissionStatus.CHANGES_REQUESTED ||
                             s.getStatus() == SubmissionStatus.REJECTED)
                .limit(5)
                .map(submissionService::mapToDto)
                .toList();
        dto.setRecentFeedback(feedbackList);

        // Activity timeline
        dto.setRecentActivities(activityService.getRecentActivities(userId));

        return dto;
    }
}
