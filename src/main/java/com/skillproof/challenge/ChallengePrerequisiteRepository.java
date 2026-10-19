package com.skillproof.challenge;

import com.skillproof.skill.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengePrerequisiteRepository extends JpaRepository<ChallengePrerequisite, Long> {

    List<ChallengePrerequisite> findByChallengeId(Long challengeId);

    @Query("SELECT cp.prerequisiteSkill FROM ChallengePrerequisite cp WHERE cp.challenge.id = :challengeId")
    List<Skill> findPrerequisiteSkillsForChallenge(@Param("challengeId") Long challengeId);
}
