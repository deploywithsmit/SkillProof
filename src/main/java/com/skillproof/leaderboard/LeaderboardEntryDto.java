package com.skillproof.leaderboard;

public class LeaderboardEntryDto {
    private int rank;
    private Long userId;
    private String username;
    private String fullName;
    private String avatarUrl;
    private int totalPoints;
    private int approvedChallengesCount;
    private int verifiedSkillsCount;
    private String headline;

    public LeaderboardEntryDto() {}

    public LeaderboardEntryDto(int rank, Long userId, String username, String fullName,
                               String avatarUrl, int totalPoints, int approvedChallengesCount,
                               int verifiedSkillsCount, String headline) {
        this.rank = rank;
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.totalPoints = totalPoints;
        this.approvedChallengesCount = approvedChallengesCount;
        this.verifiedSkillsCount = verifiedSkillsCount;
        this.headline = headline;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

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

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getApprovedChallengesCount() {
        return approvedChallengesCount;
    }

    public void setApprovedChallengesCount(int approvedChallengesCount) {
        this.approvedChallengesCount = approvedChallengesCount;
    }

    public int getVerifiedSkillsCount() {
        return verifiedSkillsCount;
    }

    public void setVerifiedSkillsCount(int verifiedSkillsCount) {
        this.verifiedSkillsCount = verifiedSkillsCount;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }
}
