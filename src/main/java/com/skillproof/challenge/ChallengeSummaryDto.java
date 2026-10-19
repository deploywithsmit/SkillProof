package com.skillproof.challenge;

import com.skillproof.skill.Difficulty;
import com.skillproof.submission.SubmissionStatus;

public class ChallengeSummaryDto {
    private Long id;
    private String slug;
    private Long skillId;
    private String skillName;
    private String title;
    private Difficulty difficulty;
    private int points;
    private int estimatedMinutes;
    private String summary;
    private boolean eligible;
    private SubmissionStatus latestSubmissionStatus;

    public ChallengeSummaryDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(int estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public SubmissionStatus getLatestSubmissionStatus() {
        return latestSubmissionStatus;
    }

    public void setLatestSubmissionStatus(SubmissionStatus latestSubmissionStatus) {
        this.latestSubmissionStatus = latestSubmissionStatus;
    }
}
