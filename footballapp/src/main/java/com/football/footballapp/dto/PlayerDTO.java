package com.football.footballapp.dto;
import lombok.Data;

@Data
public class PlayerDTO {

    private Long id;
    private String name;
    private String nationality;
    private Integer age;
    private String position;
    private Integer jerseyNumber;
    private String photo;

    // Stats
    private Integer goals;
    private Integer assists;
    private Integer yellowCards;
    private Integer redCards;
    private Integer appearances;

    // Instead of full team object
    private Long teamId;
    private String teamName;

    // Bonus — league info through team
    private Long leagueId;
    private String leagueName;
}