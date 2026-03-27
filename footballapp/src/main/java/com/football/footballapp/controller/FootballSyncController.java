package com.football.footballapp.controller;
import com.football.footballapp.entity.League;
import com.football.footballapp.repository.LeagueRepository;
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
    private final LeagueRepository leagueRepository;

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

    @PostMapping("/standings/{leagueApiId}/{season}")
    public ResponseEntity<String> syncStandings(
            @PathVariable int leagueApiId,
            @PathVariable int season) {

        footballApiService.syncStandings(leagueApiId, season);
        return ResponseEntity.ok("Standings synced successfully");
    }
    @GetMapping("/standings/{leagueApiId}/{season}")
    public ResponseEntity<String> getStandings(
            @PathVariable int leagueApiId,
            @PathVariable int season) {

        footballApiService.syncStandings(leagueApiId, season);
        return ResponseEntity.ok("Standings fetched and saved successfully");
    }

    @GetMapping("/teams/{leagueId}/{apiLeagueId}/{season}")
    public ResponseEntity<String> syncTeamsWithSeason(
            @PathVariable Long leagueId,
            @PathVariable int apiLeagueId,
            @PathVariable int season) {
        return ResponseEntity.ok(footballApiService.syncTeams(leagueId, apiLeagueId, season));
    }
    @PostMapping("/teams/{leagueId}/{apiLeagueId}/{season}")
    public ResponseEntity<String> syncTeams(
            @PathVariable Long leagueId,
            @PathVariable int apiLeagueId, @PathVariable int season) {
        return ResponseEntity.ok(footballApiService.syncTeams(leagueId, apiLeagueId, season));
    }

    @PostMapping("/leagues")
    public ResponseEntity<String> syncLeagues() {
        return ResponseEntity.ok(footballApiService.syncLeagues());
    }
    @GetMapping("/teams/{leagueId}/{apiLeagueId}")
    public ResponseEntity<String> syncTeamsFromDb(
            @PathVariable Long leagueId,
            @PathVariable int apiLeagueId) {
        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("League not found"));
        Integer season = Integer.valueOf(league.getSeason());
        if (season == null) return ResponseEntity.badRequest().body("Season not set for this league");
        return ResponseEntity.ok(footballApiService.syncTeams(leagueId, apiLeagueId, season));
    }
    @GetMapping("/teams/debug/{apiLeagueId}/{season}")
    public ResponseEntity<Map> debugTeams(
            @PathVariable int apiLeagueId,
            @PathVariable int season) {
        return ResponseEntity.ok(footballApiService.debugTeams(apiLeagueId, season));
    }
}