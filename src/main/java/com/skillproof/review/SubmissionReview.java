package com.skillproof.review;

import com.skillproof.submission.Submission;
import com.skillproof.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "submission_reviews")
public class SubmissionReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReviewDecision decision;

    @Column(name = "total_score", nullable = false)
    private int totalScore;

    @Column(name = "general_feedback", columnDefinition = "TEXT", nullable = false)
    private String generalFeedback;

    @Column(name = "improvement_suggestions", columnDefinition = "TEXT")
    private String improvementSuggestions;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic = true;

    @Column(name = "reviewed_at", nullable = false, updatable = false)
    private Instant reviewedAt = Instant.now();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewScore> scores = new ArrayList<>();

    public SubmissionReview() {}

    public SubmissionReview(Submission submission, User reviewer, ReviewDecision decision,
                            int totalScore, String generalFeedback, String improvementSuggestions, boolean isPublic) {
        this.submission = submission;
        this.reviewer = reviewer;
        this.decision = decision;
        this.totalScore = totalScore;
        this.generalFeedback = generalFeedback;
        this.improvementSuggestions = improvementSuggestions;
        this.isPublic = isPublic;
        this.reviewedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public ReviewDecision getDecision() {
        return decision;
    }

    public void setDecision(ReviewDecision decision) {
        this.decision = decision;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
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

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Instant reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public List<ReviewScore> getScores() {
        return scores;
    }

    public void setScores(List<ReviewScore> scores) {
        this.scores = scores;
    }
}
