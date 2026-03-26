package com.football.footballapp.controller;
import com.football.footballapp.service.FootballApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class FootballSyncController {

    private final FootballApiService footballApiService;

    @GetMapping("/test")
    public ResponseEntity<Map> testConnection() {
        return ResponseEntity.ok(footballApiService.testConnection());
    }

    @PostMapping("/fixtures/{leagueId}/{apiLeagueId}/{season}")
    public ResponseEntity<String> syncFixtures(
            @PathVariable Long leagueId,
            @PathVariable int apiLeagueId,
            @PathVariable int season) {
        String result = footballApiService.syncFixtures(leagueId, apiLeagueId, season);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/leagues")
    public ResponseEntity<Map> getLeagues() {
        return ResponseEntity.ok(footballApiService.getLeaguesFromApi());
    }
}