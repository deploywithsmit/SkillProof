package com.skillproof.skill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    List<UserSkill> findByUserId(Long userId);

    Optional<UserSkill> findByUserIdAndSkillId(Long userId, Long skillId);

    boolean existsByUserIdAndSkillId(Long userId, Long skillId);

    @Query("SELECT us FROM UserSkill us WHERE us.user.id = :userId AND us.status = :status")
    List<UserSkill> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") UserSkillStatus status);

    @Query("SELECT COUNT(us) FROM UserSkill us WHERE us.user.id = :userId AND us.status = 'VERIFIED'")
    long countVerifiedSkillsByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(us) FROM UserSkill us WHERE us.user.id = :userId AND us.status = 'IN_PROGRESS'")
    long countInProgressSkillsByUser(@Param("userId") Long userId);
}
