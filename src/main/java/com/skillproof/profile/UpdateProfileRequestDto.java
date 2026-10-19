package com.skillproof.profile;

import jakarta.validation.constraints.Size;

public class UpdateProfileRequestDto {

    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    @Size(max = 120, message = "Headline cannot exceed 120 characters")
    private String headline;

    @Size(max = 2000, message = "Bio cannot exceed 2000 characters")
    private String bio;

    @Size(max = 255, message = "Technical interests cannot exceed 255 characters")
    private String technicalInterests;

    @Size(max = 255, message = "GitHub URL cannot exceed 255 characters")
    private String githubUrl;

    @Size(max = 255, message = "LinkedIn URL cannot exceed 255 characters")
    private String linkedinUrl;

    private Boolean isPublic;

    private Boolean showOnLeaderboard;

    private Boolean showReviews;

    public UpdateProfileRequestDto() {}

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public Boolean getShowOnLeaderboard() {
        return showOnLeaderboard;
    }

    public void setShowOnLeaderboard(Boolean showOnLeaderboard) {
        this.showOnLeaderboard = showOnLeaderboard;
    }

    public Boolean getShowReviews() {
        return showReviews;
    }

    public void setShowReviews(Boolean showReviews) {
        this.showReviews = showReviews;
    }
}
