package com.skillproof.profile;

import com.skillproof.review.VerifiedSkillDto;
import com.skillproof.submission.SubmissionResponseDto;

import java.util.ArrayList;
import java.util.List;

public class ProfileDto {
    private Long userId;
    private String username;
    private String fullName;
    private String avatarUrl;
    private String role;
    private String headline;
    private String bio;
    private String technicalInterests;
    private String githubUrl;
    private String linkedinUrl;
    private boolean isPublic;
    private boolean showOnLeaderboard;
    private boolean showReviews;

    // Distinguish skill tiers
    private List<VerifiedSkillDto> verifiedSkills = new ArrayList<>();
    private List<SubmissionResponseDto> approvedProjects = new ArrayList<>();
    private List<PublicReviewDto> mentorReviews = new ArrayList<>();

    public ProfileDto() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getTechnicalInterests() {
        return technicalInterests;
    }

    public void setTechnicalInterests(String technicalInterests) {
        this.technicalInterests = technicalInterests;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public boolean isShowOnLeaderboard() {
        return showOnLeaderboard;
    }

    public void setShowOnLeaderboard(boolean showOnLeaderboard) {
        this.showOnLeaderboard = showOnLeaderboard;
    }

    public boolean isShowReviews() {
        return showReviews;
    }

    public void setShowReviews(boolean showReviews) {
        this.showReviews = showReviews;
    }

    public List<VerifiedSkillDto> getVerifiedSkills() {
        return verifiedSkills;
    }

    public void setVerifiedSkills(List<VerifiedSkillDto> verifiedSkills) {
        this.verifiedSkills = verifiedSkills;
    }

    public List<SubmissionResponseDto> getApprovedProjects() {
        return approvedProjects;
    }

    public void setApprovedProjects(List<SubmissionResponseDto> approvedProjects) {
        this.approvedProjects = approvedProjects;
    }

    public List<PublicReviewDto> getMentorReviews() {
        return mentorReviews;
    }

    public void setMentorReviews(List<PublicReviewDto> mentorReviews) {
        this.mentorReviews = mentorReviews;
    }

    public static class PublicReviewDto {
        private String challengeTitle;
        private String reviewerName;
        private int totalScore;
        private String generalFeedback;

        public PublicReviewDto() {}

        public PublicReviewDto(String challengeTitle, String reviewerName, int totalScore, String generalFeedback) {
            this.challengeTitle = challengeTitle;
            this.reviewerName = reviewerName;
            this.totalScore = totalScore;
            this.generalFeedback = generalFeedback;
        }

        public String getChallengeTitle() {
            return challengeTitle;
        }

        public void setChallengeTitle(String challengeTitle) {
            this.challengeTitle = challengeTitle;
        }

        public String getReviewerName() {
            return reviewerName;
        }

        public void setReviewerName(String reviewerName) {
            this.reviewerName = reviewerName;
        }

        public int getTotalScore() {
            return totalScore;
        }

        public void setTotalScore(int totalScore) {
            this.totalScore = totalScore;
        }

        public String getGeneralFeedback() {
            return generalFeedback;
        }

        public void setGeneralFeedback(String generalFeedback) {
            this.generalFeedback = generalFeedback;
        }
    }
}
