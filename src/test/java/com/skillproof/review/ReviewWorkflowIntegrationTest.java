package com.skillproof.review;

import com.skillproof.challenge.Challenge;
import com.skillproof.notification.NotificationService;
import com.skillproof.security.UserPrincipal;
import com.skillproof.skill.Difficulty;
import com.skillproof.skill.Skill;
import com.skillproof.skill.UserSkill;
import com.skillproof.skill.UserSkillRepository;
import com.skillproof.skill.UserSkillStatus;
import com.skillproof.submission.Submission;
import com.skillproof.submission.SubmissionRepository;
import com.skillproof.submission.SubmissionStatus;
import com.skillproof.user.ActivityService;
import com.skillproof.user.Role;
import com.skillproof.user.User;
import com.skillproof.user.UserRepository;
import com.skillproof.user.UserStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewWorkflowIntegrationTest {

    @Mock private SubmissionReviewRepository reviewRepository;
    @Mock private ReviewScoreRepository reviewScoreRepository;
    @Mock private VerifiedSkillRepository verifiedSkillRepository;
    @Mock private SubmissionRepository submissionRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserSkillRepository userSkillRepository;
    @Mock private NotificationService notificationService;
    @Mock private ActivityService activityService;

    private ReviewService reviewService;

    private User mentor;
    private User student;
    private Skill testSkill;
    private Challenge testChallenge;
    private Submission testSubmission;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewService(
                reviewRepository,
                reviewScoreRepository,
                verifiedSkillRepository,
                submissionRepository,
                userRepository,
                userSkillRepository,
                notificationService,
                activityService
        );

        mentor = new User("sarah@skillproof.io", "sarah_m", "Sarah Mentor", "hash", Role.MENTOR, UserStatus.ACTIVE);
        mentor.setId(2L);

        student = new User("student@example.com", "student_k", "Student Kim", "hash", Role.STUDENT, UserStatus.ACTIVE);
        student.setId(10L);

        testSkill = new Skill("java", "Java Fundamentals", "Programming", Difficulty.BEGINNER, "Java", "OOP", 15, "code");
        testSkill.setId(100L);

        testChallenge = new Challenge();
        testChallenge.setId(500L);
        testChallenge.setTitle("Console Expense Tracker");
        testChallenge.setSkill(testSkill);
        testChallenge.setPoints(50);

        testSubmission = new Submission(testChallenge, student);
        testSubmission.setId(1000L);
        testSubmission.setStatus(SubmissionStatus.UNDER_REVIEW);
        testSubmission.setAssignedMentor(mentor);

        // Set security context for mentor
        UserPrincipal principal = new UserPrincipal(mentor);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should approve submission, record rubric scores, update user progress, and create verified skill")
    void shouldApproveSubmissionAndCreateVerifiedSkill() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(mentor));
        when(submissionRepository.findById(1000L)).thenReturn(Optional.of(testSubmission));
        when(reviewRepository.findBySubmissionId(1000L)).thenReturn(Optional.empty());
        when(reviewRepository.save(any(SubmissionReview.class))).thenAnswer(i -> i.getArgument(0));
        when(userSkillRepository.findByUserIdAndSkillId(10L, 100L)).thenReturn(Optional.of(
                new UserSkill(student, testSkill, UserSkillStatus.IN_PROGRESS, 60)
        ));
        when(verifiedSkillRepository.existsByUserIdAndSkillId(10L, 100L)).thenReturn(false);

        ReviewRequestDto request = new ReviewRequestDto();
        request.setDecision(ReviewDecision.APPROVED);
        request.setTotalScore(48);
        request.setGeneralFeedback("Clean architecture and great unit tests.");
        request.setImprovementSuggestions("Consider SQLite for scaling.");
        request.setPublic(true);
        request.setScores(List.of(
                new ReviewRequestDto.CriterionScoreItem("Architecture", 15, 15, "Clean OOP"),
                new ReviewRequestDto.CriterionScoreItem("Testing", 10, 10, "Great coverage")
        ));

        SubmissionReview review = reviewService.submitReview(1000L, request);

        assertThat(review).isNotNull();
        assertThat(review.getDecision()).isEqualTo(ReviewDecision.APPROVED);
        assertThat(testSubmission.getStatus()).isEqualTo(SubmissionStatus.APPROVED);

        // Verify rubric scores persisted
        verify(reviewScoreRepository, times(2)).save(any(ReviewScore.class));

        // Verify user skill updated to VERIFIED
        verify(userSkillRepository).save(argThat(us ->
                us.getStatus() == UserSkillStatus.VERIFIED && us.getProgressPercent() == 100
        ));

        // Verify verified badge generated
        verify(verifiedSkillRepository).save(argThat(vs ->
                vs.getVerificationBadgeCode().startsWith("VP-JAVA-") &&
                vs.getSkill().equals(testSkill) &&
                vs.getUser().equals(student)
        ));

        // Verify notification and activity logged
        verify(notificationService).notifyUser(eq(student), contains("Skill Verified"), anyString(), eq("SKILL_VERIFIED"), eq("/verified.html"));
        verify(activityService).logActivity(eq(student), eq("SKILL_VERIFIED"), anyString(), eq(1000L));
    }

    @Test
    @DisplayName("Should request changes on submission and notify student without verifying skill")
    void shouldRequestChangesWithoutVerifying() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(mentor));
        when(submissionRepository.findById(1000L)).thenReturn(Optional.of(testSubmission));
        when(reviewRepository.findBySubmissionId(1000L)).thenReturn(Optional.empty());
        when(reviewRepository.save(any(SubmissionReview.class))).thenAnswer(i -> i.getArgument(0));

        ReviewRequestDto request = new ReviewRequestDto();
        request.setDecision(ReviewDecision.CHANGES_REQUESTED);
        request.setTotalScore(25);
        request.setGeneralFeedback("Good start, but missing JUnit test cases.");

        reviewService.submitReview(1000L, request);

        assertThat(testSubmission.getStatus()).isEqualTo(SubmissionStatus.CHANGES_REQUESTED);

        // Ensure NO verified skill is created
        verify(verifiedSkillRepository, never()).save(any(VerifiedSkill.class));
        verify(notificationService).notifyUser(eq(student), contains("Changes Requested"), anyString(), eq("CHANGES_REQUESTED"), anyString());
    }
}
