package com.skillproof.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionReviewRepository extends JpaRepository<SubmissionReview, Long> {
    Optional<SubmissionReview> findBySubmissionId(Long submissionId);
    List<SubmissionReview> findByReviewerIdOrderByReviewedAtDesc(Long reviewerId);
}
