package com.skillproof.challenge;

import com.skillproof.skill.Skill;
import jakarta.persistence.*;

@Entity
@Table(name = "challenge_prerequisites", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"challenge_id", "prerequisite_skill_id"})
})
public class ChallengePrerequisite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prerequisite_skill_id", nullable = false)
    private Skill prerequisiteSkill;

    public ChallengePrerequisite() {}

    public ChallengePrerequisite(Challenge challenge, Skill prerequisiteSkill) {
        this.challenge = challenge;
        this.prerequisiteSkill = prerequisiteSkill;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public Skill getPrerequisiteSkill() {
        return prerequisiteSkill;
    }

    public void setPrerequisiteSkill(Skill prerequisiteSkill) {
        this.prerequisiteSkill = prerequisiteSkill;
    }
}
