package com.football.footballapp.service;
import com.football.footballapp.dto.FixtureDTO;
import com.football.footballapp.dto.HeadToHeadDTO;
import com.football.footballapp.entity.Fixture;
import com.football.footballapp.entity.Team;
import com.football.footballapp.entity.League;
import com.football.footballapp.enums.FixtureStatus;
import com.football.footballapp.exception.ResourceNotFoundException;
import com.football.footballapp.repository.FixtureRepository;
import com.football.footballapp.repository.TeamRepository;
import com.football.footballapp.repository.LeagueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FixtureService {

    private final FixtureRepository fixtureRepository;
    private final TeamRepository teamRepository;
    private final LeagueRepository leagueRepository;

    private FixtureDTO convertToDTO(Fixture fixture) {
        FixtureDTO dto = new FixtureDTO();
        dto.setId(fixture.getId());

        // Home team
        dto.setHomeTeamId(fixture.getHomeTeam().getId());
        dto.setHomeTeamName(fixture.getHomeTeam().getName());
        dto.setHomeTeamLogo(fixture.getHomeTeam().getLogo());

        // Away team
        dto.setAwayTeamId(fixture.getAwayTeam().getId());
        dto.setAwayTeamName(fixture.getAwayTeam().getName());
        dto.setAwayTeamLogo(fixture.getAwayTeam().getLogo());

        // League
        dto.setLeagueId(fixture.getLeague().getId());
        dto.setLeagueName(fixture.getLeague().getName());

        // Match details
        dto.setMatchDate(fixture.getMatchDate());
        dto.setStatus(fixture.getStatus());
        dto.setHomeScore(fixture.getHomeScore());
        dto.setAwayScore(fixture.getAwayScore());
        dto.setVenue(fixture.getVenue());
        dto.setReferee(fixture.getReferee());

        return dto;
    }

    public List<FixtureDTO> getAllFixtures() {
        return fixtureRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FixtureDTO getFixtureById(Long id) {
        Fixture fixture = fixtureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fixture not found with id: " + id));
        return convertToDTO(fixture);
    }

    public List<FixtureDTO> getFixturesByLeague(Long leagueId) {
        return fixtureRepository.findByLeagueId(leagueId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FixtureDTO> getFixturesByTeam(Long teamId) {
        return fixtureRepository.findByTeamId(teamId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FixtureDTO> getLiveFixtures() {
        return fixtureRepository.findByStatus(FixtureStatus.LIVE)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FixtureDTO> getFixturesByLeagueAndStatus(Long leagueId, FixtureStatus status) {
        return fixtureRepository.findByLeagueIdAndStatus(leagueId, status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Fixture createFixture(Long homeTeamId, Long awayTeamId,
                                 Long leagueId, Fixture fixture) {
        Team homeTeam = teamRepository.findById(homeTeamId)
                .orElseThrow(() ->new ResourceNotFoundException("Home team not found with id: " + homeTeamId));

        Team awayTeam = teamRepository.findById(awayTeamId)
                .orElseThrow(() ->new ResourceNotFoundException("Away team not found with id: " + awayTeamId));


        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() ->new ResourceNotFoundException("League not found with id: " + leagueId));


        fixture.setHomeTeam(homeTeam);
        fixture.setAwayTeam(awayTeam);
        fixture.setLeague(league);
        fixture.setStatus(FixtureStatus.SCHEDULED);

        return fixtureRepository.save(fixture);
    }

    public FixtureDTO updateScore(Long id, Integer homeScore, Integer awayScore) {
        Fixture fixture = fixtureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fixture not found with id: " + id));

        fixture.setHomeScore(homeScore);
        fixture.setAwayScore(awayScore);
        fixture.setStatus(FixtureStatus.FINISHED);

        return convertToDTO(fixtureRepository.save(fixture));
    }

    public FixtureDTO updateStatus(Long id, FixtureStatus status) {
        Fixture fixture = fixtureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fixture not found with id: " + id));;
        fixture.setStatus(status);
        return convertToDTO(fixtureRepository.save(fixture));
    }

    public HeadToHeadDTO getHeadToHead(Long team1Id, Long team2Id) {
        Team team1 = teamRepository.findById(team1Id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + team1Id));
        Team team2 = teamRepository.findById(team2Id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + team2Id));

        List<Fixture> fixtures = fixtureRepository.findHeadToHead(team1Id, team2Id);

        HeadToHeadDTO h2h = new HeadToHeadDTO();
        h2h.setTeam1Name(team1.getName());
        h2h.setTeam1Logo(team1.getLogo());
        h2h.setTeam2Name(team2.getName());
        h2h.setTeam2Logo(team2.getLogo());

        int team1Wins = 0, team2Wins = 0, draws = 0;
        int team1Goals = 0, team2Goals = 0;

        for (Fixture f : fixtures) {
            if (f.getStatus().name().equals("FINISHED")) {
                boolean isTeam1Home = f.getHomeTeam().getId().equals(team1Id);
                int t1Goals = isTeam1Home ? f.getHomeScore() : f.getAwayScore();
                int t2Goals = isTeam1Home ? f.getAwayScore() : f.getHomeScore();

                team1Goals += t1Goals;
                team2Goals += t2Goals;

                if (t1Goals > t2Goals) team1Wins++;
                else if (t2Goals > t1Goals) team2Wins++;
                else draws++;
            }
        }

        h2h.setTeam1Wins(team1Wins);
        h2h.setTeam2Wins(team2Wins);
        h2h.setDraws(draws);
        h2h.setTotalMatches(team1Wins + team2Wins + draws);
        h2h.setTeam1Goals(team1Goals);
        h2h.setTeam2Goals(team2Goals);
        h2h.setRecentMatches(fixtures.stream()
                .limit(5)
                .map(this::convertToDTO)
                .toList());

        return h2h;
    }

    public void deleteFixture(Long id) {
        fixtureRepository.deleteById(id);
    }
}

