package com.skillproof.challenge;

import com.skillproof.skill.Difficulty;
import com.skillproof.skill.Skill;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "challenges")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(nullable = false, length = 150)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Difficulty difficulty;

    @Column(nullable = false)
    private int points = 50;

    @Column(name = "estimated_minutes", nullable = false)
    private int estimatedMinutes = 60;

    @Column(nullable = false, length = 300)
    private String summary;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String instructions;

    @Column(name = "submission_requirements", columnDefinition = "TEXT", nullable = false)
    private String submissionRequirements;

    @Column(name = "rubric_json", columnDefinition = "TEXT", nullable = false)
    private String rubricJson;

    @Column(name = "sample_input_output", columnDefinition = "TEXT")
    private String sampleInputOutput;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChallengePrerequisite> prerequisites = new ArrayList<>();

    public Challenge() {}

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

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
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

    public String getRubricJson() {
        return rubricJson;
    }

    public void setRubricJson(String rubricJson) {
        this.rubricJson = rubricJson;
    }

    public String getSampleInputOutput() {
        return sampleInputOutput;
    }

    public void setSampleInputOutput(String sampleInputOutput) {
        this.sampleInputOutput = sampleInputOutput;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ChallengePrerequisite> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<ChallengePrerequisite> prerequisites) {
        this.prerequisites = prerequisites;
    }
}
