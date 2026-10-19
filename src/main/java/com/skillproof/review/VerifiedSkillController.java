package com.skillproof.review;

import com.skillproof.common.ApiResponse;
import com.skillproof.security.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/verified-skills")
public class VerifiedSkillController {

    private final ReviewService reviewService;

    public VerifiedSkillController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<List<VerifiedSkillDto>>> getMyVerifiedSkills() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<VerifiedSkillDto> list = reviewService.getVerifiedSkillsForUser(userId);
        return ResponseEntity.ok(ApiResponse.ok("Verified skills retrieved", list));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<VerifiedSkillDto>>> getUserVerifiedSkills(@PathVariable Long userId) {
        List<VerifiedSkillDto> list = reviewService.getVerifiedSkillsForUser(userId);
        return ResponseEntity.ok(ApiResponse.ok("Verified skills retrieved", list));
    }

    @GetMapping("/badge/{badgeCode}")
    public ResponseEntity<ApiResponse<VerifiedSkillDto>> getByBadgeCode(@PathVariable String badgeCode) {
        VerifiedSkillDto dto = reviewService.getVerifiedSkillByBadgeCode(badgeCode);
        return ResponseEntity.ok(ApiResponse.ok("Verified credential found", dto));
    }
}
