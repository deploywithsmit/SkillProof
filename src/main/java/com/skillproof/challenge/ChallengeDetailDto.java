package com.skillproof.challenge;

import com.skillproof.skill.Difficulty;
import com.skillproof.skill.SkillPrerequisiteDto;
import com.skillproof.submission.SubmissionStatus;

import java.util.ArrayList;
import java.util.List;

public class ChallengeDetailDto {
    private Long id;
    private String slug;
    private Long skillId;
    private String skillName;
    private String skillCategory;
    private String title;
    private Difficulty difficulty;
    private int points;
    private int estimatedMinutes;
    private String summary;
    private String description;
    private String instructions;
    private String submissionRequirements;
    private List<RubricCriterionDto> rubric = new ArrayList<>();
    private String sampleInputOutput;
    private boolean eligible;
    private List<SkillPrerequisiteDto> prerequisiteSkills = new ArrayList<>();
    private SubmissionStatus latestSubmissionStatus;
    private Long latestSubmissionId;

    public ChallengeDetailDto() {}

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

    public String getSkillCategory() {
        return skillCategory;
    }

    public void setSkillCategory(String skillCategory) {
        this.skillCategory = skillCategory;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getSubmissionRequirements() {
        return submissionRequirements;
    }

    public void setSubmissionRequirements(String submissionRequirements) {
        this.submissionRequirements = submissionRequirements;
    }

    public List<RubricCriterionDto> getRubric() {
        return rubric;
    }

    public void setRubric(List<RubricCriterionDto> rubric) {
        this.rubric = rubric;
    }

    public String getSampleInputOutput() {
        return sampleInputOutput;
    }

    public void setSampleInputOutput(String sampleInputOutput) {
        this.sampleInputOutput = sampleInputOutput;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public List<SkillPrerequisiteDto> getPrerequisiteSkills() {
        return prerequisiteSkills;
    }

    public void setPrerequisiteSkills(List<SkillPrerequisiteDto> prerequisiteSkills) {
        this.prerequisiteSkills = prerequisiteSkills;
    }

    public SubmissionStatus getLatestSubmissionStatus() {
        return latestSubmissionStatus;
    }

    public void setLatestSubmissionStatus(SubmissionStatus latestSubmissionStatus) {
        this.latestSubmissionStatus = latestSubmissionStatus;
    }

    public Long getLatestSubmissionId() {
        return latestSubmissionId;
    }

    public void setLatestSubmissionId(Long latestSubmissionId) {
        this.latestSubmissionId = latestSubmissionId;
    }
}
