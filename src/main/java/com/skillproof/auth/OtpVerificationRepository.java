package com.skillproof.auth;

import com.skillproof.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByUserAndPurposeAndConsumedAtIsNullOrderByCreatedAtDesc(User user, OtpPurpose purpose);

    @Modifying
    @Query("UPDATE OtpVerification o SET o.consumedAt = :now WHERE o.user = :user AND o.purpose = :purpose AND o.consumedAt IS NULL")
    void invalidateActiveOtps(@Param("user") User user, @Param("purpose") OtpPurpose purpose, @Param("now") Instant now);
}
