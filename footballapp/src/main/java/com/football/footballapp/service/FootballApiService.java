package com.football.footballapp.service;
import com.football.footballapp.entity.Fixture;
import com.football.footballapp.entity.League;
import com.football.footballapp.entity.Team;
import com.football.footballapp.enums.FixtureStatus;
import com.football.footballapp.repository.FixtureRepository;
import com.football.footballapp.repository.LeagueRepository;
import com.football.footballapp.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FootballApiService {

    private final RestTemplate restTemplate;
    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;
    private final FixtureRepository fixtureRepository;

    @Value("${football.api.key}")
    private String apiKey;

    @Value("${football.api.host}")
    private String apiHost;

    @Value("${football.api.url}")
    private String apiUrl;

    // Build headers for every API call
    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-apisports-key", apiKey);
        headers.set("x-apisports-host", apiHost); // 🔥 ADD THIS
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // Generic GET call to football API
    private Map callApi(String endpoint) {
        HttpEntity<String> entity = new HttpEntity<>(buildHeaders());
        ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl + endpoint,
                HttpMethod.GET,
                entity,
                Map.class
        );
        return response.getBody();
    }

    // Fetch and save standings for a league
    public void syncStandings(int leagueApiId, int season) {
        Map response = callApi("/standings?league=" + leagueApiId + "&season=" + season);
        List<Map> responseList = (List<Map>) response.get("response");

        if (responseList == null || responseList.isEmpty()) return;

        Map leagueData = (Map) responseList.get(0);
        Map leagueInfo = (Map) leagueData.get("league");
        List<List<Map>> standingsData = (List<List<Map>>) leagueInfo.get("standings");

        if (standingsData == null || standingsData.isEmpty()) return;

        for (Map teamData : standingsData.get(0)) {
            Map teamInfo = (Map) teamData.get("team");
            String teamName = (String) teamInfo.get("name");
            String teamLogo = (String) teamInfo.get("logo");
            int apiTeamId = (int) teamInfo.get("id");

            // Save team if not exists
            if (teamRepository.searchByName(teamName).isEmpty()) {
                Team team = new Team();
                team.setName(teamName);
                team.setLogo(teamLogo);
                team.setCountry("England");

                // Find league in our DB
                leagueRepository.findById((long) leagueApiId).ifPresent(team::setLeague);
                teamRepository.save(team);
            }
        }
    }

    public Map testConnection() {
        return callApi("/status");
    }

    // Fetch and save fixtures for a league
    public String syncFixtures(Long leagueId, int leagueApiId, int season) {
        Map response = callApi("/fixtures?league=" + leagueApiId + "&season=" + season);
        List<Map> fixtures = (List<Map>) response.get("response");

        if (fixtures == null) return "No fixtures found";

        int saved = 0;

        for (Map fixtureData : fixtures) {
            try {
                Map fixtureInfo = (Map) fixtureData.get("fixture");
                Map teamsData = (Map) fixtureData.get("teams");
                Map goalsData = (Map) fixtureData.get("goals");
                Map statusData = (Map) fixtureInfo.get("status");

                Map homeTeamData = (Map) teamsData.get("home");
                Map awayTeamData = (Map) teamsData.get("away");

                String homeTeamName = (String) homeTeamData.get("name");
                String awayTeamName = (String) awayTeamData.get("name");

                // Find teams in our DB
                List<Team> homeTeams = teamRepository.searchByName(homeTeamName);
                List<Team> awayTeams = teamRepository.searchByName(awayTeamName);

                if (homeTeams.isEmpty() || awayTeams.isEmpty()) continue;

                Team homeTeam = homeTeams.get(0);
                Team awayTeam = awayTeams.get(0);

                // Parse date
                String dateStr = (String) fixtureInfo.get("date");
                LocalDateTime matchDate = LocalDateTime.parse(
                        dateStr.substring(0, 19),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                );

                // Parse status
                String statusShort = (String) statusData.get("short");
                FixtureStatus status = switch (statusShort) {
                    case "FT", "AET", "PEN" -> FixtureStatus.FINISHED;
                    case "1H", "2H", "HT", "ET", "LIVE" -> FixtureStatus.LIVE;
                    case "PST" -> FixtureStatus.POSTPONED;
                    case "CANC" -> FixtureStatus.CANCELLED;
                    default -> FixtureStatus.SCHEDULED;
                };

                // Get league
                League league = leagueRepository.findById(leagueId).orElse(null);
                if (league == null) continue;

                // Create fixture
                Fixture fixture = new Fixture();
                fixture.setHomeTeam(homeTeam);
                fixture.setAwayTeam(awayTeam);
                fixture.setLeague(league);
                fixture.setMatchDate(matchDate);
                fixture.setStatus(status);
                fixture.setVenue((String) ((Map) fixtureInfo.get("venue")).get("name"));

                // Set scores if finished
                if (status == FixtureStatus.FINISHED && goalsData != null) {
                    Object homeGoals = goalsData.get("home");
                    Object awayGoals = goalsData.get("away");
                    if (homeGoals != null) fixture.setHomeScore((Integer) homeGoals);
                    if (awayGoals != null) fixture.setAwayScore((Integer) awayGoals);
                }

                fixtureRepository.save(fixture);
                saved++;

            } catch (Exception e) {
                System.out.println("Skipped fixture: " + e.getMessage());
            }
        }

        return "Saved " + saved + " fixtures";
    }

    // Fetch leagues info
    public Map getLeaguesFromApi() {
        return callApi("/leagues?current=true");
    }


}

//"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwZ3JheXNvbjAxMUBnbWFpbC5jb20iLCJpYXQiOjE3NzQ1NDM2NzksImV4cCI6MTc3NDYzMDA3OX0.IMTiD0kjpzmdzXq4ZKM7Lx4mEUCDKqQZfmfT11CIRDs"