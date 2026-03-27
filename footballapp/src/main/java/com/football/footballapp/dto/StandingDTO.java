package com.football.footballapp.dto;

import lombok.Data;

@Data
public class StandingDTO {
    private Long teamId;
    private String teamName;
    private String teamLogo;
    private int position, played, won, drawn, lost;
    private int goalsFor, goalsAgainst, goalDifference, points;
    private String form;
}