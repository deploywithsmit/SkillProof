package com.skillproof.skill;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findBySlug(String slug);

    List<Skill> findByCategory(String category);

    @Query("SELECT s FROM Skill s WHERE " +
           "(:category IS NULL OR LOWER(s.category) = LOWER(:category)) AND " +
           "(:difficulty IS NULL OR s.difficulty = :difficulty) AND " +
           "(:search IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(s.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Skill> searchSkills(@Param("category") String category,
                            @Param("difficulty") Difficulty difficulty,
                            @Param("search") String search,
                            Pageable pageable);

    @Query("SELECT DISTINCT s.category FROM Skill s ORDER BY s.category ASC")
    List<String> findAllCategories();
}
