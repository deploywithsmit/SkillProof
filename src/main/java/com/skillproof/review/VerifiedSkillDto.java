package com.skillproof.review;

import com.skillproof.skill.Difficulty;
import java.time.Instant;

public class VerifiedSkillDto {

    private Long id;
    private Long skillId;
    private String skillSlug;
    private String skillName;
    private String skillCategory;
    private Difficulty skillDifficulty;
    private String iconName;
    private Long studentId;
    private String studentName;
    private String studentUsername;
    private String reviewerName;
    private String verificationBadgeCode;
    private Instant verifiedAt;
    private Long submissionId;
    private String challengeTitle;
    private Integer reviewScore;

    public VerifiedSkillDto() {}

    public VerifiedSkillDto(VerifiedSkill vs, String challengeTitle, Integer reviewScore) {
        this.id = vs.getId();
        this.skillId = vs.getSkill().getId();
        this.skillSlug = vs.getSkill().getSlug();
        this.skillName = vs.getSkill().getName();
        this.skillCategory = vs.getSkill().getCategory();
        this.skillDifficulty = vs.getSkill().getDifficulty();
        this.iconName = vs.getSkill().getIconName();
        this.studentId = vs.getUser().getId();
        this.studentName = vs.getUser().getFullName();
        this.studentUsername = vs.getUser().getUsername();
        this.reviewerName = vs.getReviewer().getFullName();
        this.verificationBadgeCode = vs.getVerificationBadgeCode();
        this.verifiedAt = vs.getVerifiedAt();
        this.submissionId = vs.getVerifiedBySubmission().getId();
        this.challengeTitle = challengeTitle;
        this.reviewScore = reviewScore;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getSkillSlug() {
        return skillSlug;
    }

    public void setSkillSlug(String skillSlug) {
        this.skillSlug = skillSlug;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillCategory() {
        return skillCategory;
    }

    public void setSkillCategory(String skillCategory) {
        this.skillCategory = skillCategory;
    }

    public Difficulty getSkillDifficulty() {
        return skillDifficulty;
    }

    public void setSkillDifficulty(Difficulty skillDifficulty) {
        this.skillDifficulty = skillDifficulty;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
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

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getVerificationBadgeCode() {
        return verificationBadgeCode;
    }

    public void setVerificationBadgeCode(String verificationBadgeCode) {
        this.verificationBadgeCode = verificationBadgeCode;
    }

    public Instant getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(Instant verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public String getChallengeTitle() {
        return challengeTitle;
    }

    public void setChallengeTitle(String challengeTitle) {
        this.challengeTitle = challengeTitle;
    }

    public Integer getReviewScore() {
        return reviewScore;
    }

    public void setReviewScore(Integer reviewScore) {
        this.reviewScore = reviewScore;
    }
}
