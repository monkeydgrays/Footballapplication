package com.football.footballapp.service;
import com.football.footballapp.dto.PlayerDTO;
import com.football.footballapp.entity.Player;
import com.football.footballapp.entity.Team;
import com.football.footballapp.exception.ResourceNotFoundException;
import com.football.footballapp.repository.PlayerRepository;
import com.football.footballapp.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    // Convert Entity → DTO
    private PlayerDTO convertToDTO(Player player) {
        PlayerDTO dto = new PlayerDTO();
        dto.setId(player.getId());
        dto.setName(player.getName());
        dto.setNationality(player.getNationality());
        dto.setAge(player.getAge());
        dto.setPosition(player.getPosition());
        dto.setJerseyNumber(player.getJerseyNumber());
        dto.setPhoto(player.getPhoto());
        dto.setGoals(player.getGoals());
        dto.setAssists(player.getAssists());
        dto.setYellowCards(player.getYellowCards());
        dto.setRedCards(player.getRedCards());
        dto.setAppearances(player.getAppearances());

        // Team info
        dto.setTeamId(player.getTeam().getId());
        dto.setTeamName(player.getTeam().getName());

        // League info through team
        dto.setLeagueId(player.getTeam().getLeague().getId());
        dto.setLeagueName(player.getTeam().getLeague().getName());

        return dto;
    }

    public List<PlayerDTO> getAllPlayers() {
        return playerRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PlayerDTO getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + id));
        return convertToDTO(player);
    }

    public List<PlayerDTO> getPlayersByTeam(Long teamId) {
        return playerRepository.findByTeamId(teamId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PlayerDTO> getPlayersByPosition(String position) {
        return playerRepository.findByPosition(position)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PlayerDTO> getTopScorers() {
        return playerRepository.findAllByOrderByGoalsDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Player createPlayer(Long teamId, Player player) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() ->new ResourceNotFoundException("Team not found with id: " + teamId));
        team.getLeague();
        player.setTeam(team);
        return playerRepository.save(player);
    }

    public PlayerDTO updatePlayerStats(Long id, Player updatedStats) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() ->new ResourceNotFoundException("Player not found with id: " + id));

        // Only update stats fields
        player.setGoals(updatedStats.getGoals());
        player.setAssists(updatedStats.getAssists());
        player.setYellowCards(updatedStats.getYellowCards());
        player.setRedCards(updatedStats.getRedCards());
        player.setAppearances(updatedStats.getAppearances());

        return convertToDTO(playerRepository.save(player));
    }

    public void deletePlayer(Long id) {
        playerRepository.deleteById(id);
    }
}