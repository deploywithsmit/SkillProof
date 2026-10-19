package com.skillproof.skill;

import java.util.List;

public class SkillRecommendationDto {
    private Long skillId;
    private String slug;
    private String name;
    private String category;
    private Difficulty difficulty;
    private String explanation;
    private boolean eligible;
    private List<String> missingPrerequisites;
    private String iconName;

    public SkillRecommendationDto() {}

    public SkillRecommendationDto(SkillDependencyEngine.SkillRecommendation rec) {
        this.skillId = rec.skill().getId();
        this.slug = rec.skill().getSlug();
        this.name = rec.skill().getName();
        this.category = rec.skill().getCategory();
        this.difficulty = rec.skill().getDifficulty();
        this.explanation = rec.explanation();
        this.eligible = rec.isEligible();
        this.missingPrerequisites = rec.missingPrerequisites();
        this.iconName = rec.skill().getIconName();
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
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

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public List<String> getMissingPrerequisites() {
        return missingPrerequisites;
    }

    public void setMissingPrerequisites(List<String> missingPrerequisites) {
        this.missingPrerequisites = missingPrerequisites;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
}
