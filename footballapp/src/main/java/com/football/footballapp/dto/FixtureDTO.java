package com.football.footballapp.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.football.footballapp.enums.FixtureStatus;
import lombok.Data;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;

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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime matchDate; // ✅ React can parse this

    @JsonProperty("status")
    private String status;
    private Integer homeScore;
    private Integer awayScore;
    private String venue;
    private String referee;


}
