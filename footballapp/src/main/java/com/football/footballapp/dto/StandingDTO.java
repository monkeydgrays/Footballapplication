package com.football.footballapp.dto;

import lombok.Data;

@Data
public class StandingDTO {
    private Long teamId;
    private String teamName;
    private String teamLogo;
    private int position;
    private int played;
    private int won;
    private int drawn;
    private int lost;
    private int goalsFor;
    private int goalsAgainst;
    private int goalDifference;
    private int points;
    private String form;
}