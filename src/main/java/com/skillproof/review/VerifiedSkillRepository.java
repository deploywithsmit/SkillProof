package com.skillproof.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VerifiedSkillRepository extends JpaRepository<VerifiedSkill, Long> {

    List<VerifiedSkill> findByUserIdOrderByVerifiedAtDesc(Long userId);

    Optional<VerifiedSkill> findByUserIdAndSkillId(Long userId, Long skillId);

    Optional<VerifiedSkill> findByVerificationBadgeCode(String verificationBadgeCode);

    boolean existsByUserIdAndSkillId(Long userId, Long skillId);

    @Query("SELECT COUNT(vs) FROM VerifiedSkill vs WHERE vs.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}
