package com.skillproof.dashboard;

import com.skillproof.challenge.ChallengeSummaryDto;
import com.skillproof.skill.SkillRecommendationDto;
import com.skillproof.skill.SkillSummaryDto;
import com.skillproof.submission.SubmissionResponseDto;
import com.skillproof.user.UserActivity;

import java.util.ArrayList;
import java.util.List;

public class DashboardOverviewDto {

    private String welcomeName;
    private String username;
    private String role;
    private long skillsPursuedCount;
    private long challengesCompletedCount;
    private long pendingSubmissionsCount;
    private long verifiedSkillsCount;
    private int totalPoints;

    private List<SkillSummaryDto> inProgressSkills = new ArrayList<>();
    private List<SkillRecommendationDto> recommendations = new ArrayList<>();
    private List<SubmissionResponseDto> recentFeedback = new ArrayList<>();
    private List<UserActivity> recentActivities = new ArrayList<>();

    public DashboardOverviewDto() {}

    public String getWelcomeName() {
        return welcomeName;
    }

    public void setWelcomeName(String welcomeName) {
        this.welcomeName = welcomeName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getSkillsPursuedCount() {
        return skillsPursuedCount;
    }

    public void setSkillsPursuedCount(long skillsPursuedCount) {
        this.skillsPursuedCount = skillsPursuedCount;
    }

    public long getChallengesCompletedCount() {
        return challengesCompletedCount;
    }

    public void setChallengesCompletedCount(long challengesCompletedCount) {
        this.challengesCompletedCount = challengesCompletedCount;
    }

    public long getPendingSubmissionsCount() {
        return pendingSubmissionsCount;
    }

    public void setPendingSubmissionsCount(long pendingSubmissionsCount) {
        this.pendingSubmissionsCount = pendingSubmissionsCount;
    }

    public long getVerifiedSkillsCount() {
        return verifiedSkillsCount;
    }

    public void setVerifiedSkillsCount(long verifiedSkillsCount) {
        this.verifiedSkillsCount = verifiedSkillsCount;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public List<SkillSummaryDto> getInProgressSkills() {
        return inProgressSkills;
    }

    public void setInProgressSkills(List<SkillSummaryDto> inProgressSkills) {
        this.inProgressSkills = inProgressSkills;
    }

    public List<SkillRecommendationDto> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<SkillRecommendationDto> recommendations) {
        this.recommendations = recommendations;
    }

    public List<SubmissionResponseDto> getRecentFeedback() {
        return recentFeedback;
    }

    public void setRecentFeedback(List<SubmissionResponseDto> recentFeedback) {
        this.recentFeedback = recentFeedback;
    }

    public List<UserActivity> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<UserActivity> recentActivities) {
        this.recentActivities = recentActivities;
    }
}
