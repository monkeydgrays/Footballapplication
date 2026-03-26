package com.football.footballapp.repository;
import com.football.footballapp.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    // Get all players in a team
    List<Player> findByTeamId(Long teamId);

    // Get all players by position
    List<Player> findByPosition(String position);

    // Get all players by nationality
    List<Player> findByNationality(String nationality);

    // Get top scorers - ordered by goals descending
    List<Player> findAllByOrderByGoalsDesc();

    @Query("SELECT p FROM Player p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Player> searchByName(String keyword);

}
