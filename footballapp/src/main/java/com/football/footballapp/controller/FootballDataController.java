package com.football.footballapp.controller;
import com.football.footballapp.dto.FixtureDTO;
import com.football.footballapp.dto.StandingDTO;
import com.football.footballapp.dto.PlayerDTO;
import com.football.footballapp.entity.Fixture;
import com.football.footballapp.entity.Team;
import com.football.footballapp.enums.FixtureStatus;
import com.football.footballapp.repository.*;
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
    private  final PlayerRepository playerRepository;


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
        return ResponseEntity.ok(
                standingRepository.findByLeagueIdOrderByPositionAsc(leagueId)
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
                        }).toList()
        );
    }
    @GetMapping("/teams/{teamId}/players")
    public ResponseEntity<?> getPlayersByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(
                playerRepository.findByTeamId(teamId).stream().map(p -> {
                    PlayerDTO dto = new PlayerDTO();
                    dto.setId(p.getId());
                    dto.setName(p.getName());
                    dto.setPhoto(p.getPhoto());
                    dto.setNationality(p.getNationality());
                    dto.setPosition(p.getPosition());
                    dto.setAge(p.getAge());
                    dto.setJerseyNumber(p.getJerseyNumber());
                    dto.setGoals(p.getGoals());
                    dto.setAssists(p.getAssists());
                    dto.setYellowCards(p.getYellowCards());
                    dto.setRedCards(p.getRedCards());
                    dto.setAppearances(p.getAppearances());
                    dto.setTeamId(p.getTeam().getId());
                    dto.setTeamName(p.getTeam().getName());
                    dto.setLeagueId(p.getTeam().getLeague().getId());
                    dto.setLeagueName(p.getTeam().getLeague().getName());
                    return dto;
                }).toList()
        );
    }
}
