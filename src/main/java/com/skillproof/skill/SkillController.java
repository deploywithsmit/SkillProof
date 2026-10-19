package com.skillproof.skill;

import com.skillproof.common.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SkillSummaryDto>>> getSkills(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<SkillSummaryDto> result = skillService.searchSkills(category, difficulty, search, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Skills retrieved successfully", result));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        List<String> categories = skillService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.ok("Categories retrieved", categories));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse<List<SkillRecommendationDto>>> getRecommendations() {
        List<SkillRecommendationDto> recommendations = skillService.getRecommendations();
        return ResponseEntity.ok(ApiResponse.ok("Skill recommendations generated", recommendations));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillDetailDto>> getSkillDetails(@PathVariable Long id) {
        SkillDetailDto detail = skillService.getSkillDetails(id);
        return ResponseEntity.ok(ApiResponse.ok("Skill details retrieved", detail));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<SkillDetailDto>> getSkillBySlug(@PathVariable String slug) {
        SkillDetailDto detail = skillService.getSkillBySlug(slug);
        return ResponseEntity.ok(ApiResponse.ok("Skill details retrieved", detail));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<String>> startPursuingSkill(@PathVariable Long id) {
        skillService.startPursuingSkill(id);
        return ResponseEntity.ok(ApiResponse.ok("You are now pursuing this skill! Check out the associated challenges to earn proof."));
    }
}
