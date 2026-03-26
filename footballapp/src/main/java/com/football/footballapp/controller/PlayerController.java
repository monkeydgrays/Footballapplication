package com.football.footballapp.controller;
import com.football.footballapp.dto.PlayerDTO;
import com.football.footballapp.entity.Player;
import com.football.footballapp.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping
    public ResponseEntity<List<PlayerDTO>> getAllPlayers() {
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.getPlayerById(id));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<PlayerDTO>> getPlayersByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(playerService.getPlayersByTeam(teamId));
    }

    @GetMapping("/position/{position}")
    public ResponseEntity<List<PlayerDTO>> getPlayersByPosition(@PathVariable String position) {
        return ResponseEntity.ok(playerService.getPlayersByPosition(position));
    }

    @GetMapping("/top-scorers")
    public ResponseEntity<List<PlayerDTO>> getTopScorers() {
        return ResponseEntity.ok(playerService.getTopScorers());
    }

    @PostMapping("/team/{teamId}")
    public ResponseEntity<PlayerDTO> createPlayer(
            @PathVariable Long teamId,
            @RequestBody Player player) {
        Player created = playerService.createPlayer(teamId, player);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(playerService.getPlayerById(created.getId()));
    }

    @PutMapping("/{id}/stats")
    public ResponseEntity<PlayerDTO> updatePlayerStats(
            @PathVariable Long id,
            @RequestBody Player stats) {
        return ResponseEntity.ok(playerService.updatePlayerStats(id, stats));
    }

    @GetMapping("/compare")
    public ResponseEntity<Map<String, Object>> comparePlayers(
            @RequestParam Long player1Id,
            @RequestParam Long player2Id) {
        Map<String, Object> comparison = new HashMap<>();
        comparison.put("player1", playerService.getPlayerById(player1Id));
        comparison.put("player2", playerService.getPlayerById(player2Id));
        return ResponseEntity.ok(comparison);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }
}
