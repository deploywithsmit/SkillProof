package com.skillproof.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewScoreRepository extends JpaRepository<ReviewScore, Long> {
    List<ReviewScore> findByReviewId(Long reviewId);
}
