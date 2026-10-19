package com.skillproof.skill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillPrerequisiteRepository extends JpaRepository<SkillPrerequisite, Long> {

    List<SkillPrerequisite> findBySkillId(Long skillId);

    List<SkillPrerequisite> findByPrerequisiteId(Long prerequisiteId);

    boolean existsBySkillIdAndPrerequisiteId(Long skillId, Long prerequisiteId);

    @Query("SELECT sp.prerequisite FROM SkillPrerequisite sp WHERE sp.skill.id = :skillId")
    List<Skill> findPrerequisitesForSkill(@Param("skillId") Long skillId);

    @Query("SELECT sp.skill FROM SkillPrerequisite sp WHERE sp.prerequisite.id = :skillId")
    List<Skill> findDependentSkillsForSkill(@Param("skillId") Long skillId);
}
