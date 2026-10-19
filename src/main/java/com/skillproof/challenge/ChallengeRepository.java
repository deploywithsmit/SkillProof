package com.skillproof.challenge;

import com.skillproof.skill.Difficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    Optional<Challenge> findBySlug(String slug);

    List<Challenge> findBySkillId(Long skillId);

    @Query("SELECT c FROM Challenge c WHERE " +
           "(:skillId IS NULL OR c.skill.id = :skillId) AND " +
           "(:difficulty IS NULL OR c.difficulty = :difficulty) AND " +
           "(:search IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.summary) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Challenge> searchChallenges(@Param("skillId") Long skillId,
                                    @Param("difficulty") Difficulty difficulty,
                                    @Param("search") String search,
                                    Pageable pageable);
}
