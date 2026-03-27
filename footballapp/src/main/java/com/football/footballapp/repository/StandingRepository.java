package com.football.footballapp.repository;

import com.football.footballapp.entity.Standing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StandingRepository extends JpaRepository<Standing, Long> {
    List<Standing> findByLeagueIdOrderByPositionAsc(Long leagueId);
    void deleteByLeagueId(Long leagueId);
}