package com.football.footballapp.dto;
import com.football.footballapp.enums.FixtureStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FixtureDTO {

    private Long id;

    // Home team
    private Long homeTeamId;
    private String homeTeamName;
    private String homeTeamLogo;

    // Away team
    private Long awayTeamId;
    private String awayTeamName;
    private String awayTeamLogo;

    // League
    private Long leagueId;
    private String leagueName;

    // Match details
    private LocalDateTime matchDate;
    private FixtureStatus status;
    private Integer homeScore;
    private Integer awayScore;
    private String venue;
    private String referee;
}
