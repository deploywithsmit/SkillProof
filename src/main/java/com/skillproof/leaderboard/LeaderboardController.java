package com.skillproof.leaderboard;

import com.skillproof.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LeaderboardEntryDto>>> getLeaderboard() {
        List<LeaderboardEntryDto> leaderboard = leaderboardService.getLeaderboard();
        return ResponseEntity.ok(ApiResponse.ok("Leaderboard ranking retrieved", leaderboard));
    }

    @GetMapping("/formula")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFormula() {
        Map<String, Object> formula = leaderboardService.getScoringFormula();
        return ResponseEntity.ok(ApiResponse.ok("Leaderboard scoring formula", formula));
    }
}
