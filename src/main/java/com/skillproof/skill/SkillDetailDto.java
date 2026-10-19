package com.skillproof.skill;

import java.util.ArrayList;
import java.util.List;

public class SkillDetailDto {
    private Long id;
    private String slug;
    private String name;
    private String category;
    private Difficulty difficulty;
    private String description;
    private String learningObjectives;
    private int estimatedHours;
    private String iconName;
    private UserSkillStatus userStatus;
    private Integer progressPercent;
    private boolean eligible;
    private List<SkillPrerequisiteDto> prerequisites = new ArrayList<>();
    private List<SkillPrerequisiteDto> unlockedSkills = new ArrayList<>();

    public SkillDetailDto() {}

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLearningObjectives() {
        return learningObjectives;
    }

    public void setLearningObjectives(String learningObjectives) {
        this.learningObjectives = learningObjectives;
    }

    public int getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(int estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public UserSkillStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserSkillStatus userStatus) {
        this.userStatus = userStatus;
    }

    public Integer getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(Integer progressPercent) {
        this.progressPercent = progressPercent;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public List<SkillPrerequisiteDto> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<SkillPrerequisiteDto> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public List<SkillPrerequisiteDto> getUnlockedSkills() {
        return unlockedSkills;
    }

    public void setUnlockedSkills(List<SkillPrerequisiteDto> unlockedSkills) {
        this.unlockedSkills = unlockedSkills;
    }
}
