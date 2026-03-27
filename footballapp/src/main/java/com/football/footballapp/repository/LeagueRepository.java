package com.football.footballapp.repository;

import com.football.footballapp.entity.League;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {
    Optional<League> findByApiId(Integer apiId);
    @Query("SELECT l FROM League l WHERE LOWER(l.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<League> searchByName(String keyword);
    Optional<League> findByName(String name);

}