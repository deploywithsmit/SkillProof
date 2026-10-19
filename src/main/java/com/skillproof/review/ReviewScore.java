package com.skillproof.review;

import jakarta.persistence.*;

@Entity
@Table(name = "review_scores")
public class ReviewScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private SubmissionReview review;

    @Column(name = "criterion_name", nullable = false, length = 100)
    private String criterionName;

    @Column(nullable = false)
    private int score;

    @Column(name = "max_score", nullable = false)
    private int maxScore;

    @Column(length = 255)
    private String comments;

    public ReviewScore() {}

    public ReviewScore(SubmissionReview review, String criterionName, int score, int maxScore, String comments) {
        this.review = review;
        this.criterionName = criterionName;
        this.score = score;
        this.maxScore = maxScore;
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubmissionReview getReview() {
        return review;
    }

    public void setReview(SubmissionReview review) {
        this.review = review;
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
