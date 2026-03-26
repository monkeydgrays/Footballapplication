package com.football.footballapp.dto;

import lombok.Data;

@Data
public class TeamDTO {

    private Long id;
    private String name;
    private String logo;
    private String stadium;
    private String manager;
    private String country;
    private Integer foundedYear;

    // Instead of full league object, just these two
    private Long leagueId;
    private String leagueName;
}