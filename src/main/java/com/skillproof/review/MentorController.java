package com.skillproof.review;

import com.skillproof.common.ApiResponse;
import com.skillproof.submission.Submission;
import com.skillproof.submission.SubmissionResponseDto;
import com.skillproof.submission.SubmissionService;
import com.skillproof.submission.SubmissionStatus;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mentor")
@PreAuthorize("hasAnyRole('MENTOR', 'ADMIN')")
public class MentorController {

    private final ReviewService reviewService;
    private final SubmissionService submissionService;

    public MentorController(ReviewService reviewService, SubmissionService submissionService) {
        this.reviewService = reviewService;
        this.submissionService = submissionService;
    }

    @GetMapping("/submissions")
    public ResponseEntity<ApiResponse<Page<SubmissionResponseDto>>> getQueue(
            @RequestParam(required = false) SubmissionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Submission> queue = reviewService.getMentorQueue(status, pageable);
        Page<SubmissionResponseDto> dtos = queue.map(submissionService::mapToDto);

        return ResponseEntity.ok(ApiResponse.ok("Review queue retrieved", dtos));
    }

    @PostMapping("/submissions/{submissionId}/reviews")
    public ResponseEntity<ApiResponse<SubmissionReview>> submitReview(
            @PathVariable Long submissionId,
            @Valid @RequestBody ReviewRequestDto request) {

        SubmissionReview review = reviewService.submitReview(submissionId, request);
        return ResponseEntity.ok(ApiResponse.ok("Review submitted successfully", review));
    }
}
