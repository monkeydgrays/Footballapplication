package com.football.footballapp.repository;
import com.football.footballapp.entity.Fixture;
import com.football.footballapp.enums.FixtureStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FixtureRepository extends JpaRepository<Fixture, Long> {

    // Get all fixtures in a league
    List<Fixture> findByLeagueId(Long leagueId);

    // Get all fixtures by status
    List<Fixture> findByStatus(FixtureStatus status);

    // Get all fixtures for a team (home or away)
    @Query("SELECT f FROM Fixture f WHERE f.homeTeam.id = :teamId OR f.awayTeam.id = :teamId")
    List<Fixture> findByTeamId(Long teamId);

    // Get fixtures by league and status
    List<Fixture> findByLeagueIdAndStatus(Long leagueId, FixtureStatus status);

    @Query("SELECT f FROM Fixture f WHERE " +
            "(f.homeTeam.id = :team1Id AND f.awayTeam.id = :team2Id) OR " +
            "(f.homeTeam.id = :team2Id AND f.awayTeam.id = :team1Id) " +
            "ORDER BY f.matchDate DESC")
    List<Fixture> findHeadToHead(Long team1Id, Long team2Id);
}