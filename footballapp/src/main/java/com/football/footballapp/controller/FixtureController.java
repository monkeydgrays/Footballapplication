package com.football.footballapp.controller;
import com.football.footballapp.dto.FixtureDTO;
import com.football.footballapp.dto.HeadToHeadDTO;
import com.football.footballapp.entity.Fixture;
import com.football.footballapp.enums.FixtureStatus;
import com.football.footballapp.service.FixtureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fixtures")
@RequiredArgsConstructor
public class FixtureController {

    private final FixtureService fixtureService;

    @GetMapping
    public ResponseEntity<List<FixtureDTO>> getAllFixtures() {
        return ResponseEntity.ok(fixtureService.getAllFixtures());
    }

    @GetMapping("/h2h")
    public ResponseEntity<HeadToHeadDTO> getHeadToHead(
            @RequestParam Long team1Id,
            @RequestParam Long team2Id) {
        return ResponseEntity.ok(fixtureService.getHeadToHead(team1Id, team2Id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FixtureDTO> getFixtureById(@PathVariable Long id) {
        return ResponseEntity.ok(fixtureService.getFixtureById(id));
    }

    @GetMapping("/league/{leagueId}")
    public ResponseEntity<List<FixtureDTO>> getFixturesByLeague(@PathVariable Long leagueId) {
        return ResponseEntity.ok(fixtureService.getFixturesByLeague(leagueId));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<FixtureDTO>> getFixturesByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(fixtureService.getFixturesByTeam(teamId));
    }

    @GetMapping("/live")
    public ResponseEntity<List<FixtureDTO>> getLiveFixtures() {
        return ResponseEntity.ok(fixtureService.getLiveFixtures());
    }

    @GetMapping("/league/{leagueId}/status/{status}")
    public ResponseEntity<List<FixtureDTO>> getFixturesByLeagueAndStatus(
            @PathVariable Long leagueId,
            @PathVariable FixtureStatus status) {
        return ResponseEntity.ok(fixtureService.getFixturesByLeagueAndStatus(leagueId, status));
    }

    @PostMapping
    public ResponseEntity<FixtureDTO> createFixture(
            @RequestParam Long homeTeamId,
            @RequestParam Long awayTeamId,
            @RequestParam Long leagueId,
            @RequestBody Fixture fixture) {
        Fixture created = fixtureService.createFixture(homeTeamId, awayTeamId, leagueId, fixture);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fixtureService.getFixtureById(created.getId()));
    }

    @PutMapping("/{id}/score")
    public ResponseEntity<FixtureDTO> updateScore(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> scores) {
        return ResponseEntity.ok(fixtureService.updateScore(
                id,
                scores.get("homeScore"),
                scores.get("awayScore")
        ));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<FixtureDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam FixtureStatus status) {
        return ResponseEntity.ok(fixtureService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFixture(@PathVariable Long id) {
        fixtureService.deleteFixture(id);
        return ResponseEntity.noContent().build();
    }
}
