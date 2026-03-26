package com.football.footballapp.dto;
import lombok.Data;
import java.util.List;

@Data
public class HeadToHeadDTO {
    private String team1Name;
    private String team1Logo;
    private String team2Name;
    private String team2Logo;
    private int team1Wins;
    private int team2Wins;
    private int draws;
    private int totalMatches;
    private int team1Goals;
    private int team2Goals;
    private List<FixtureDTO> recentMatches;
}