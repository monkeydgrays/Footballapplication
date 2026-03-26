package com.football.footballapp.service;
import com.football.footballapp.entity.League;
import com.football.footballapp.exception.ResourceNotFoundException;
import com.football.footballapp.repository.LeagueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@RequiredArgsConstructor
public class LeagueService {

    private final LeagueRepository leagueRepository;
    public LeagueService(LeagueRepository leagueRepository) {
        this.leagueRepository = leagueRepository;
    }

    public List<League> getAllLeagues() {
        return leagueRepository.findAll();
    }

    public League getLeagueById(Long id) {
        return leagueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("League not found with id: " + id));
    }

    public League createLeague(League league) {
        return leagueRepository.save(league);
    }

    public void deleteLeague(Long id) {
        leagueRepository.deleteById(id);
    }
}
