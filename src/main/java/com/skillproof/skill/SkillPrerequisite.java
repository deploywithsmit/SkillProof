package com.skillproof.skill;

import jakarta.persistence.*;

@Entity
@Table(name = "skill_prerequisites", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"skill_id", "prerequisite_id"})
})
public class SkillPrerequisite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prerequisite_id", nullable = false)
    private Skill prerequisite;

    public SkillPrerequisite() {}

    public SkillPrerequisite(Skill skill, Skill prerequisite) {
        this.skill = skill;
        this.prerequisite = prerequisite;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Skill getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(Skill prerequisite) {
        this.prerequisite = prerequisite;
    }
}
