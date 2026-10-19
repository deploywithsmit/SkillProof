package com.skillproof.challenge;

import com.skillproof.common.ApiResponse;
import com.skillproof.skill.Difficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ChallengeSummaryDto>>> getChallenges(
            @RequestParam(required = false) Long skillId,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "points") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ChallengeSummaryDto> results = challengeService.searchChallenges(skillId, difficulty, search, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Challenges retrieved", results));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChallengeDetailDto>> getChallengeDetails(@PathVariable Long id) {
        ChallengeDetailDto detail = challengeService.getChallengeDetails(id);
        return ResponseEntity.ok(ApiResponse.ok("Challenge details retrieved", detail));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<ChallengeDetailDto>> getChallengeBySlug(@PathVariable String slug) {
        ChallengeDetailDto detail = challengeService.getChallengeBySlug(slug);
        return ResponseEntity.ok(ApiResponse.ok("Challenge details retrieved", detail));
    }
}
