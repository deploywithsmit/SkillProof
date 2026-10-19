package com.skillproof.review;

import com.skillproof.skill.Skill;
import com.skillproof.submission.Submission;
import com.skillproof.user.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "verified_skills", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "skill_id"}),
        @UniqueConstraint(columnNames = {"verification_badge_code"})
})
public class VerifiedSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "verified_by_submission_id", nullable = false)
    private Submission verifiedBySubmission;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @Column(name = "verification_badge_code", nullable = false, length = 64)
    private String verificationBadgeCode;

    @Column(name = "verified_at", nullable = false, updatable = false)
    private Instant verifiedAt = Instant.now();

    public VerifiedSkill() {}

    public VerifiedSkill(User user, Skill skill, Submission verifiedBySubmission, User reviewer, String verificationBadgeCode) {
        this.user = user;
        this.skill = skill;
        this.verifiedBySubmission = verifiedBySubmission;
        this.reviewer = reviewer;
        this.verificationBadgeCode = verificationBadgeCode;
        this.verifiedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Submission getVerifiedBySubmission() {
        return verifiedBySubmission;
    }

    public void setVerifiedBySubmission(Submission verifiedBySubmission) {
        this.verifiedBySubmission = verifiedBySubmission;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public String getVerificationBadgeCode() {
        return verificationBadgeCode;
    }

    public void setVerificationBadgeCode(String verificationBadgeCode) {
        this.verificationBadgeCode = verificationBadgeCode;
    }

    public Instant getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(Instant verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
}
