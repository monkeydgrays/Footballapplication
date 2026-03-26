package com.football.footballapp.controller;
import com.football.footballapp.entity.League;
import com.football.footballapp.entity.Team;
import com.football.footballapp.repository.LeagueRepository;
import com.football.footballapp.repository.PlayerRepository;
import com.football.footballapp.repository.TeamRepository;
import com.football.footballapp.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final PlayerService playerService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam String keyword) {

        Map<String, Object> results = new HashMap<>();

        List<League> leagues = leagueRepository.searchByName(keyword);
        List<Team> teams = teamRepository.searchByName(keyword);
        var players = playerRepository.searchByName(keyword)
                .stream()
                .map(p -> playerService.getPlayerById(p.getId()))
                .toList();

        results.put("leagues", leagues);
        results.put("teams", teams);
        results.put("players", players);
        results.put("total", leagues.size() + teams.size() + players.size());

        return ResponseEntity.ok(results);
    }
}
