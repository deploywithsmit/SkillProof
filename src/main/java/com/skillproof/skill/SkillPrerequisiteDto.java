package com.skillproof.skill;

public class SkillPrerequisiteDto {
    private Long id;
    private String slug;
    private String name;
    private Difficulty difficulty;
    private boolean completed;

    public SkillPrerequisiteDto() {}

    public SkillPrerequisiteDto(Long id, String slug, String name, Difficulty difficulty, boolean completed) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.difficulty = difficulty;
        this.completed = completed;
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

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
