package com.skillproof.submission;

import com.skillproof.common.ApiResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping(value = "/challenges/{challengeId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<SubmissionResponseDto>> submitChallenge(
            @PathVariable Long challengeId,
            @RequestParam(required = false) String repoUrl,
            @RequestParam(required = false) String projectUrl,
            @RequestParam(required = false) String explanation,
            @RequestParam(defaultValue = "false") boolean isDraft,
            @RequestParam(required = false) MultipartFile file) {

        SubmissionRequestDto request = new SubmissionRequestDto();
        request.setRepoUrl(repoUrl);
        request.setProjectUrl(projectUrl);
        request.setExplanation(explanation);
        request.setDraft(isDraft);

        SubmissionResponseDto response = submissionService.createOrUpdateSubmission(challengeId, request, file);
        String message = isDraft ? "Submission draft saved successfully" : "Challenge evidence submitted for review";
        return ResponseEntity.ok(ApiResponse.ok(message, response));
    }

    @PostMapping(value = "/challenges/{challengeId}/json", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ApiResponse<SubmissionResponseDto>> submitChallengeJson(
            @PathVariable Long challengeId,
            @RequestBody SubmissionRequestDto request) {

        SubmissionResponseDto response = submissionService.createOrUpdateSubmission(challengeId, request, null);
        String message = request.isDraft() ? "Submission draft saved successfully" : "Challenge evidence submitted for review";
        return ResponseEntity.ok(ApiResponse.ok(message, response));
    }

    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<Page<SubmissionResponseDto>>> getMySubmissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SubmissionResponseDto> results = submissionService.getMySubmissions(pageable);
        return ResponseEntity.ok(ApiResponse.ok("Submissions retrieved", results));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubmissionResponseDto>> getSubmission(@PathVariable Long id) {
        SubmissionResponseDto result = submissionService.getSubmissionDetails(id);
        return ResponseEntity.ok(ApiResponse.ok("Submission details retrieved", result));
    }

    @GetMapping("/{id}/attachment")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long id) {
        Resource file = submissionService.loadAttachment(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}
