package com.skillproof.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileSettingsRepository extends JpaRepository<ProfileSettings, Long> {
    Optional<ProfileSettings> findByUserId(Long userId);
}
