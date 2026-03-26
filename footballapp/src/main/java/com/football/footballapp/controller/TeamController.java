package com.football.footballapp.controller;
import com.football.footballapp.dto.TeamDTO;
import com.football.footballapp.entity.Team;
import com.football.footballapp.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    @GetMapping("/league/{leagueId}")
    public ResponseEntity<List<TeamDTO>> getTeamsByLeague(@PathVariable Long leagueId) {
        return ResponseEntity.ok(teamService.getTeamsByLeague(leagueId));
    }

    @PostMapping("/league/{leagueId}")
    public ResponseEntity<Team> createTeam(
            @PathVariable Long leagueId,
            @RequestBody Team team) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teamService.createTeam(leagueId, team));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }
}
