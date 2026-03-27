package com.football.footballapp.controller;
import com.football.footballapp.dto.FixtureDTO;
import com.football.footballapp.dto.StandingDTO;
import com.football.footballapp.entity.Fixture;
import com.football.footballapp.entity.Team;
import com.football.footballapp.enums.FixtureStatus;
import com.football.footballapp.repository.FixtureRepository;
import com.football.footballapp.repository.LeagueRepository;
import com.football.footballapp.repository.StandingRepository;
import com.football.footballapp.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FootballDataController {

    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;
    private final FixtureRepository fixtureRepository;
    private final StandingRepository standingRepository;


    @GetMapping("/leagues/{leagueId}/teams")
    public ResponseEntity<?> getTeamsByLeague(@PathVariable Long leagueId) {
        return ResponseEntity.ok(teamRepository.findByLeagueId(leagueId));
    }

    @GetMapping("/teams/{teamId}/fixtures")
    public ResponseEntity<?> getFixturesByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(
                fixtureRepository.findByHomeTeamIdOrAwayTeamId(teamId, teamId)
        );
    }

//    @GetMapping("/leagues/{leagueId}")
//    public ResponseEntity<?> getLeague(@PathVariable Long leagueId) {
//        return ResponseEntity.ok(leagueRepository.findById(leagueId)
//                .orElseThrow(() -> new RuntimeException("League not found")));
//    }
    
    @GetMapping("/standings/league/{leagueId}")
    public ResponseEntity<?> getStandings(@PathVariable Long leagueId) {
        List<StandingDTO> result = standingRepository
                .findByLeagueIdOrderByPositionAsc(leagueId)
                .stream().map(s -> {
                    StandingDTO dto = new StandingDTO();
                    dto.setTeamId(s.getTeam().getId());
                    dto.setTeamName(s.getTeam().getName());
                    dto.setTeamLogo(s.getTeam().getLogo());
                    dto.setPosition(s.getPosition());
                    dto.setPlayed(s.getPlayed());
                    dto.setWon(s.getWon());
                    dto.setDrawn(s.getDrawn());
                    dto.setLost(s.getLost());
                    dto.setGoalsFor(s.getGoalsFor());
                    dto.setGoalsAgainst(s.getGoalsAgainst());
                    dto.setGoalDifference(s.getGoalDifference());
                    dto.setPoints(s.getPoints());
                    dto.setForm(s.getForm());
                    return dto;
                }).toList();
        return ResponseEntity.ok(result);
    }
}