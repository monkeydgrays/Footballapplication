package com.football.footballapp.controller;
import com.football.footballapp.entity.League;
import com.football.footballapp.repository.LeagueRepository;
import com.football.footballapp.service.LeagueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leagues")
//@RequiredArgsConstructor
public class LeagueController {

    private final LeagueService leagueService;
    private final LeagueRepository leagueRepository;
    public LeagueController(LeagueService leagueService, LeagueRepository leagueRepository) {
        this.leagueService = leagueService;
        this.leagueRepository = leagueRepository;
    }

    @GetMapping
    public ResponseEntity<List<League>> getAllLeagues() {
        return ResponseEntity.ok(leagueService.getAllLeagues());
    }

    @GetMapping("/{id}")
    public League getLeagueById(Long id) {
        return leagueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("League not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<League> createLeague(@RequestBody League league) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(leagueService.createLeague(league));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeague(@PathVariable Long id) {
        leagueService.deleteLeague(id);
        return ResponseEntity.noContent().build();
    }
}
