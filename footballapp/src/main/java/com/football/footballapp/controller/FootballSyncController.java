package com.football.footballapp.controller;
import com.football.footballapp.dto.FixtureDTO;
import com.football.footballapp.dto.TeamDTO;
import com.football.footballapp.entity.League;
import com.football.footballapp.repository.FixtureRepository;
import com.football.footballapp.repository.LeagueRepository;
import com.football.footballapp.repository.TeamRepository;
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
    private final TeamRepository teamRepository;
    private final FixtureRepository fixtureRepository;

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

    @GetMapping("/leagues/{leagueId}/teams")
    public ResponseEntity<?> getTeamsByLeague(@PathVariable Long leagueId) {
        return ResponseEntity.ok(
                teamRepository.findByLeagueId(leagueId)
                        .stream()
                        .map(t -> {
                            TeamDTO dto = new TeamDTO();
                            dto.setId(t.getId());
                            dto.setName(t.getName());
                            dto.setLogo(t.getLogo());
                            dto.setStadium(t.getStadium());
                            dto.setCountry(t.getCountry());
                            dto.setFoundedYear(t.getFoundedYear());
                            return dto;
                        }).toList()
        );
    }
    @GetMapping("/leagues/{leagueId}/fixtures")
    public ResponseEntity<?> getFixturesByLeague(@PathVariable Long leagueId) {
        return ResponseEntity.ok(
                fixtureRepository.findByLeagueId(leagueId)
                        .stream()
                        .map(f -> {
                            FixtureDTO dto = new FixtureDTO();
                            dto.setId(f.getId());
                            dto.setHomeTeamName(f.getHomeTeam().getName());
                            dto.setAwayTeamName(f.getAwayTeam().getName());
                            dto.setHomeTeamLogo(f.getHomeTeam().getLogo());
                            dto.setAwayTeamLogo(f.getAwayTeam().getLogo());
                            dto.setMatchDate(f.getMatchDate());
                            dto.setStatus(f.getStatus().name());
                            dto.setHomeScore(f.getHomeScore());
                            dto.setAwayScore(f.getAwayScore());
                            dto.setVenue(f.getVenue());
                            return dto;
                        }).toList()
        );
    }
}