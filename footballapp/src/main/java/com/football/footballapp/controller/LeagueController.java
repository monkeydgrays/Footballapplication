package com.football.footballapp.controller;
import com.football.footballapp.entity.League;
import com.football.footballapp.service.FootballApiService;
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
    private final FootballApiService footballApiService;
    public LeagueController(LeagueService leagueService, FootballApiService footballApiService) {
        this.leagueService = leagueService;
        this.footballApiService = footballApiService;
    }

    @GetMapping
    public ResponseEntity<List<League>> getAllLeagues() {
        return ResponseEntity.ok(leagueService.getAllLeagues());
    }

    @GetMapping("/{id}")
    public ResponseEntity<League> getLeagueById(@PathVariable Long id) {
        return ResponseEntity.ok(leagueService.getLeagueById(id));
    }

//    @PostMapping
//    public ResponseEntity<League> createLeague(@RequestBody League league) {
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(leagueService.createLeague(league));
//    }
@PostMapping("/leagues")
public ResponseEntity<String> syncLeagues() {
    return ResponseEntity.ok(footballApiService.syncLeagues());
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeague(@PathVariable Long id) {
        leagueService.deleteLeague(id);
        return ResponseEntity.noContent().build();
    }
}
