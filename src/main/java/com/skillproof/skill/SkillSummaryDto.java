package com.skillproof.skill;

public class SkillSummaryDto {
    private Long id;
    private String slug;
    private String name;
    private String category;
    private Difficulty difficulty;
    private String description;
    private int estimatedHours;
    private String iconName;
    private UserSkillStatus userStatus;
    private Integer progressPercent;
    private boolean eligible;
    private int prerequisiteCount;

    public SkillSummaryDto() {}

    public SkillSummaryDto(Skill skill, UserSkill userSkill, boolean eligible, int prerequisiteCount) {
        this.id = skill.getId();
        this.slug = skill.getSlug();
        this.name = skill.getName();
        this.category = skill.getCategory();
        this.difficulty = skill.getDifficulty();
        this.description = skill.getDescription();
        this.estimatedHours = skill.getEstimatedHours();
        this.iconName = skill.getIconName();
        if (userSkill != null) {
            this.userStatus = userSkill.getStatus();
            this.progressPercent = userSkill.getProgressPercent();
        } else {
            this.userStatus = null;
            this.progressPercent = 0;
        }
        this.eligible = eligible;
        this.prerequisiteCount = prerequisiteCount;
    }

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

    public int getPrerequisiteCount() {
        return prerequisiteCount;
    }

    public void setPrerequisiteCount(int prerequisiteCount) {
        this.prerequisiteCount = prerequisiteCount;
    }
}
