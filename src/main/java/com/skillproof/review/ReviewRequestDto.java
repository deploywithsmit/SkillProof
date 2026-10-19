package com.skillproof.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ReviewRequestDto {

    @NotNull(message = "Review decision is required")
    private ReviewDecision decision;

    @NotNull(message = "Total score is required")
    private Integer totalScore;

    @NotBlank(message = "General feedback is required")
    private String generalFeedback;

    private String improvementSuggestions;

    private boolean isPublic = true;

    private List<CriterionScoreItem> scores = new ArrayList<>();

    public ReviewRequestDto() {}

    public ReviewDecision getDecision() {
        return decision;
    }

    public void setDecision(ReviewDecision decision) {
        this.decision = decision;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public String getGeneralFeedback() {
        return generalFeedback;
    }

    public void setGeneralFeedback(String generalFeedback) {
        this.generalFeedback = generalFeedback;
    }

    public String getImprovementSuggestions() {
        return improvementSuggestions;
    }

    public void setImprovementSuggestions(String improvementSuggestions) {
        this.improvementSuggestions = improvementSuggestions;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public List<CriterionScoreItem> getScores() {
        return scores;
    }

    public void setScores(List<CriterionScoreItem> scores) {
        this.scores = scores;
    }

    public static class CriterionScoreItem {
        private String criterionName;
        private int score;
        private int maxScore;
        private String comments;

        public CriterionScoreItem() {}

        public CriterionScoreItem(String criterionName, int score, int maxScore, String comments) {
            this.criterionName = criterionName;
            this.score = score;
            this.maxScore = maxScore;
            this.comments = comments;
        }

        public String getCriterionName() {
            return criterionName;
        }

        public void setCriterionName(String criterionName) {
            this.criterionName = criterionName;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getMaxScore() {
            return maxScore;
        }

        public void setMaxScore(int maxScore) {
            this.maxScore = maxScore;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }
    }
}
