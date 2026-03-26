package com.football.footballapp.service;
import com.football.footballapp.dto.TeamDTO;
import com.football.footballapp.entity.Team;
import com.football.footballapp.entity.League;
import com.football.footballapp.exception.ResourceNotFoundException;
import com.football.footballapp.repository.TeamRepository;
import com.football.footballapp.repository.LeagueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final LeagueRepository leagueRepository;

    // Convert Entity → DTO
    private TeamDTO convertToDTO(Team team) {
        TeamDTO dto = new TeamDTO();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setLogo(team.getLogo());
        dto.setStadium(team.getStadium());
        dto.setManager(team.getManager());
        dto.setCountry(team.getCountry());
        dto.setFoundedYear(team.getFoundedYear());
        dto.setLeagueId(team.getLeague().getId());
        dto.setLeagueName(team.getLeague().getName());
        return dto;
    }

    public List<TeamDTO> getAllTeams() {
        return teamRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TeamDTO getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() ->new ResourceNotFoundException("Team not found with id: " + id));
        return convertToDTO(team);
    }

    public List<TeamDTO> getTeamsByLeague(Long leagueId) {
        return teamRepository.findByLeagueId(leagueId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Team createTeam(Long leagueId, Team team) {
        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new ResourceNotFoundException("League not found with id: " + leagueId));

        team.setLeague(league);
        return teamRepository.save(team);
    }

    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }
}