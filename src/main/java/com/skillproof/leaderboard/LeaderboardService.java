package com.skillproof.leaderboard;

import com.skillproof.profile.ProfileSettings;
import com.skillproof.profile.ProfileSettingsRepository;
import com.skillproof.review.VerifiedSkillRepository;
import com.skillproof.submission.Submission;
import com.skillproof.submission.SubmissionRepository;
import com.skillproof.submission.SubmissionStatus;
import com.skillproof.user.Role;
import com.skillproof.user.User;
import com.skillproof.user.UserRepository;
import com.skillproof.user.UserStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class LeaderboardService {

    private final UserRepository userRepository;
    private final ProfileSettingsRepository profileSettingsRepository;
    private final SubmissionRepository submissionRepository;
    private final VerifiedSkillRepository verifiedSkillRepository;

    public LeaderboardService(UserRepository userRepository,
                              ProfileSettingsRepository profileSettingsRepository,
                              SubmissionRepository submissionRepository,
                              VerifiedSkillRepository verifiedSkillRepository) {
        this.userRepository = userRepository;
        this.profileSettingsRepository = profileSettingsRepository;
        this.submissionRepository = submissionRepository;
        this.verifiedSkillRepository = verifiedSkillRepository;
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntryDto> getLeaderboard() {
        List<User> students = userRepository.findAll().stream()
                .filter(u -> u.getStatus() == UserStatus.ACTIVE && u.getRole() == Role.STUDENT)
                .toList();

        List<LeaderboardEntryDto> entries = new ArrayList<>();

        for (User student : students) {
            Optional<ProfileSettings> settingsOpt = profileSettingsRepository.findByUserId(student.getId());

            // Respect privacy and leaderboard opt-out
            boolean optedIn = settingsOpt.map(ProfileSettings::isShowOnLeaderboard).orElse(true);
            boolean isPublic = settingsOpt.map(ProfileSettings::isPublic).orElse(true);

            if (!optedIn || !isPublic) {
                continue;
            }

            // Calculate points:
            // 1. Approved submissions (distinct by challenge to prevent duplicate point farming)
            List<Submission> submissions = submissionRepository.findByUserIdOrderByCreatedAtDesc(student.getId());
            Set<Long> approvedChallengeIds = new HashSet<>();
            int basePoints = 0;

            for (Submission s : submissions) {
                if (s.getStatus() == SubmissionStatus.APPROVED) {
                    if (approvedChallengeIds.add(s.getChallenge().getId())) {
                        basePoints += s.getChallenge().getPoints();
                    }
                }
            }

            // 2. Verified skills bonus (50 pts per verified skill)
            long verifiedSkillsCount = verifiedSkillRepository.countByUserId(student.getId());
            int totalPoints = basePoints + (int) (verifiedSkillsCount * 50);

            String headline = settingsOpt.map(ProfileSettings::getHeadline).orElse("Student Engineer");

            entries.add(new LeaderboardEntryDto(
                    0, // Assigned after sorting
                    student.getId(),
                    student.getUsername(),
                    student.getFullName(),
                    student.getAvatarUrl(),
                    totalPoints,
                    approvedChallengeIds.size(),
                    (int) verifiedSkillsCount,
                    headline
            ));
        }

        // Sort descending by points, then by approved challenge count
        entries.sort((a, b) -> {
            int cmp = Integer.compare(b.getTotalPoints(), a.getTotalPoints());
            if (cmp != 0) return cmp;
            return Integer.compare(b.getApprovedChallengesCount(), a.getApprovedChallengesCount());
        });

        // Assign ranks
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setRank(i + 1);
        }

        return entries;
    }

    public Map<String, Object> getScoringFormula() {
        return Map.of(
                "title", "SkillProof Merit-Based Scoring Formula",
                "rules", List.of(
                        "Challenge Points: Full points awarded upon mentor approval of unique challenge tasks (no duplicate points for repeat submissions).",
                        "Verified Skill Credential: +50 bonus points for each officially verified skill badge.",
                        "Zero-inflation guarantee: Submissions in draft or under review earn 0 points until peer-reviewed and verified by an authorized mentor.",
                        "Fair play: Students can opt out of public leaderboard indexing at any time in their Profile Settings without losing verified badges."
                )
        );
    }
}
