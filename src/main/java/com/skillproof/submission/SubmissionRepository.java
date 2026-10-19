package com.skillproof.submission;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<Submission> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Optional<Submission> findTopByUserIdAndChallengeIdOrderByCreatedAtDesc(Long userId, Long challengeId);

    List<Submission> findByAssignedMentorIdOrderBySubmittedAtAsc(Long mentorId);

    @Query("SELECT s FROM Submission s WHERE s.status IN ('SUBMITTED', 'UNDER_REVIEW') ORDER BY s.submittedAt ASC")
    List<Submission> findPendingMentorSubmissions();

    @Query("SELECT s FROM Submission s WHERE " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:mentorId IS NULL OR s.assignedMentor.id = :mentorId) " +
           "ORDER BY s.submittedAt DESC")
    Page<Submission> searchMentorSubmissions(@Param("status") SubmissionStatus status,
                                            @Param("mentorId") Long mentorId,
                                            Pageable pageable);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.user.id = :userId AND s.status = 'APPROVED'")
    long countApprovedSubmissionsByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.user.id = :userId AND s.status IN ('SUBMITTED', 'UNDER_REVIEW')")
    long countPendingSubmissionsByUser(@Param("userId") Long userId);
}
