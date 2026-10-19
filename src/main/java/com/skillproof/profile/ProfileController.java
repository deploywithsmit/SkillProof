package com.skillproof.profile;

import com.skillproof.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileDto>> getMyProfile() {
        ProfileDto profile = profileService.getMyProfile();
        return ResponseEntity.ok(ApiResponse.ok("Profile retrieved", profile));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<ProfileDto>> updateMyProfile(@Valid @RequestBody UpdateProfileRequestDto request) {
        ProfileDto updated = profileService.updateMyProfile(request);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", updated));
    }

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<ProfileDto>> getPublicProfile(@PathVariable String username) {
        ProfileDto profile = profileService.getPublicProfile(username);
        return ResponseEntity.ok(ApiResponse.ok("Public profile retrieved", profile));
    }
}
