package com.skillproof.submission;

import com.skillproof.review.ReviewDecision;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SubmissionResponseDto {

    private Long id;
    private Long challengeId;
    private String challengeTitle;
    private String challengeSlug;
    private Long skillId;
    private String skillName;
    private Long studentId;
    private String studentName;
    private String studentUsername;
    private SubmissionStatus status;
    private String repoUrl;
    private String projectUrl;
    private String explanation;
    private String attachmentFilename;
    private Long attachmentSizeBytes;
    private Long assignedMentorId;
    private String assignedMentorName;
    private Instant submittedAt;
    private Instant createdAt;

    // Review details
    private Long reviewId;
    private ReviewDecision reviewDecision;
    private Integer totalScore;
    private String generalFeedback;
    private String improvementSuggestions;
    private String reviewerName;
    private Instant reviewedAt;
    private List<ReviewScoreDto> rubricScores = new ArrayList<>();

    public SubmissionResponseDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public String getChallengeTitle() {
        return challengeTitle;
    }

    public void setChallengeTitle(String challengeTitle) {
        this.challengeTitle = challengeTitle;
    }

    public String getChallengeSlug() {
        return challengeSlug;
    }

    public void setChallengeSlug(String challengeSlug) {
        this.challengeSlug = challengeSlug;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentUsername() {
        return studentUsername;
    }

    public void setStudentUsername(String studentUsername) {
        this.studentUsername = studentUsername;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getAttachmentFilename() {
        return attachmentFilename;
    }

    public void setAttachmentFilename(String attachmentFilename) {
        this.attachmentFilename = attachmentFilename;
    }

    public Long getAttachmentSizeBytes() {
        return attachmentSizeBytes;
    }

    public void setAttachmentSizeBytes(Long attachmentSizeBytes) {
        this.attachmentSizeBytes = attachmentSizeBytes;
    }

    public Long getAssignedMentorId() {
        return assignedMentorId;
    }

    public void setAssignedMentorId(Long assignedMentorId) {
        this.assignedMentorId = assignedMentorId;
    }

    public String getAssignedMentorName() {
        return assignedMentorName;
    }

    public void setAssignedMentorName(String assignedMentorName) {
        this.assignedMentorName = assignedMentorName;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public ReviewDecision getReviewDecision() {
        return reviewDecision;
    }

    public void setReviewDecision(ReviewDecision reviewDecision) {
        this.reviewDecision = reviewDecision;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public String getGeneralFeedback() {
        return generalFeedback;
    }

    public void setGeneralFeedback(String generalFeedback) {
        this.generalFeedback = generalFeedback;
    }

    public String getImprovementSuggestions() {
        return improvementSuggestions;
    }

    public void setImprovementSuggestions(String improvementSuggestions) {
        this.improvementSuggestions = improvementSuggestions;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Instant reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public List<ReviewScoreDto> getRubricScores() {
        return rubricScores;
    }

    public void setRubricScores(List<ReviewScoreDto> rubricScores) {
        this.rubricScores = rubricScores;
    }

    public static class ReviewScoreDto {
        private String criterionName;
        private int score;
        private int maxScore;
        private String comments;

        public ReviewScoreDto() {}

        public ReviewScoreDto(String criterionName, int score, int maxScore, String comments) {
            this.criterionName = criterionName;
            this.score = score;
            this.maxScore = maxScore;
            this.comments = comments;
        }

        public String getCriterionName() {
            return criterionName;
        }

        public void setCriterionName(String criterionName) {
            this.criterionName = criterionName;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getMaxScore() {
            return maxScore;
        }

        public void setMaxScore(int maxScore) {
            this.maxScore = maxScore;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }
    }
}
