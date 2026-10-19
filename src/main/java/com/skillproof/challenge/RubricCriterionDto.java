package com.skillproof.challenge;

public class RubricCriterionDto {
    private String criterion;
    private int maxPoints;
    private String guidance;

    public RubricCriterionDto() {}

    public RubricCriterionDto(String criterion, int maxPoints, String guidance) {
        this.criterion = criterion;
        this.maxPoints = maxPoints;
        this.guidance = guidance;
    }

    public String getCriterion() {
        return criterion;
    }

    public void setCriterion(String criterion) {
        this.criterion = criterion;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }

    public String getGuidance() {
        return guidance;
    }

    public void setGuidance(String guidance) {
        this.guidance = guidance;
    }
}
