package com.football.footballapp.service;
import com.football.footballapp.entity.Fixture;
import com.football.footballapp.entity.League;
import com.football.footballapp.entity.Team;
import com.football.footballapp.enums.FixtureStatus;
import com.football.footballapp.repository.FixtureRepository;
import com.football.footballapp.repository.LeagueRepository;
import com.football.footballapp.repository.StandingRepository;
import com.football.footballapp.entity.Standing;
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
    private final StandingRepository standingRepository;

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
    public Map debugTeams(int apiLeagueId, int season) {
        return callApi("/teams?league=" + apiLeagueId + "&season=" + season);
    }

    public String syncLeagues() {
        Map response = callApi("/leagues?current=true");
        List<Map> leagues = (List<Map>) response.get("response");

        int saved = 0;

        for (Map item : leagues) {
            Map leagueData = (Map) item.get("league");
            Map countryData = (Map) item.get("country");
            List<Map> seasons = (List<Map>) item.get("seasons");
            Integer currentSeason = null;
            if (seasons != null) {
                for (Map s : seasons) {
                    Boolean isCurrent = (Boolean) s.get("current");
                    if (Boolean.TRUE.equals(isCurrent)) {
                        currentSeason = ((Number) s.get("year")).intValue();
                        break;
                    }
                }
            }
            String name = (String) leagueData.get("name");
            String logo = (String) leagueData.get("logo");
            String country = (String) countryData.get("name");
            int apiId = ((Number) leagueData.get("id")).intValue();

            if (leagueRepository.findByApiId(apiId).isPresent() ||
                    leagueRepository.findByName(name).isPresent()) {
                continue;
            }
            League league = new League();
            league.setName(name);
            league.setLogo(logo);
            league.setCountry(country);
            league.setApiId(apiId);
            league.setSeason(String.valueOf(currentSeason));

            leagueRepository.save(league);
            saved++;
        }

        return "Saved " + saved + " leagues";
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


    public String syncTeams(Long leagueId, int apiLeagueId, int season) {
        Map response = callApi("/teams?league=" + apiLeagueId + "&season=" + season);
        List<Map> teams = (List<Map>) response.get("response");

        if (teams == null || teams.isEmpty()) return "No teams found";

        int saved = 0;

        for (Map teamData : teams) {
            try {
                Map teamInfo = (Map) teamData.get("team");
                Map venueInfo = (Map) teamData.get("venue"); // ✅ venue is here, not in teamInfo

                int apiId = ((Number) teamInfo.get("id")).intValue();
                String name = (String) teamInfo.get("name");
                String logo = (String) teamInfo.get("logo");
                String country = (String) teamInfo.get("country");
                Integer founded = teamInfo.get("founded") != null ?
                        ((Number) teamInfo.get("founded")).intValue() : null;

                // ✅ Get stadium from venue object
                String stadium = venueInfo != null ? (String) venueInfo.get("name") : null;

                if (teamRepository.findByApiId(apiId).isPresent()) continue;

                Team team = new Team();
                team.setName(name);
                team.setLogo(logo);
                team.setCountry(country);
                team.setApiId(apiId);
                team.setStadium(stadium);
                if (founded != null) team.setFoundedYear(founded);

                League league = leagueRepository.findById(leagueId).orElse(null);
                if (league == null) continue;
                team.setLeague(league);

                teamRepository.save(team);
                saved++;

            } catch (Exception e) {
                System.out.println("Skipped team: " + e.getMessage()); // ✅ see what fails
            }
        }

        return "Saved " + saved + " teams";
    }
    public void syncStandings(int leagueApiId, int season) {
        Map response = callApi("/standings?league=" + leagueApiId + "&season=" + season);
        List<Map> responseList = (List<Map>) response.get("response");
        if (responseList == null || responseList.isEmpty()) return;

        Map leagueData = (Map) responseList.get(0);
        Map leagueInfo = (Map) leagueData.get("league");
        List<List<Map>> standingsData = (List<List<Map>>) leagueInfo.get("standings");
        if (standingsData == null || standingsData.isEmpty()) return;

        League league = leagueRepository.findByApiId(leagueApiId)
                .orElseThrow(() -> new RuntimeException("League not found"));

        standingRepository.deleteByLeagueId(league.getId());

        for (Map teamData : standingsData.get(0)) {
            Map teamInfo = (Map) teamData.get("team");
            Map allData = (Map) teamData.get("all");
            Map goalsData = (Map) allData.get("goals");

            int apiTeamId = ((Number) teamInfo.get("id")).intValue();
            Team team = teamRepository.findByApiId(apiTeamId).orElse(null);
            if (team == null) continue;

            Standing s = new Standing();
            s.setLeague(league);
            s.setTeam(team);
            s.setPosition((int) teamData.get("rank"));
            s.setPoints(((Number) teamData.get("points")).intValue());
            s.setPlayed(((Number) allData.get("played")).intValue());
            s.setWon(((Number) allData.get("win")).intValue());
            s.setDrawn(((Number) allData.get("draw")).intValue());
            s.setLost(((Number) allData.get("lose")).intValue());
            s.setGoalsFor(((Number) goalsData.get("for")).intValue());
            s.setGoalsAgainst(((Number) goalsData.get("against")).intValue());
            s.setGoalDifference(s.getGoalsFor() - s.getGoalsAgainst());
            String rawForm = (String) teamData.get("form"); // "WWDLW"
            s.setForm(rawForm);
            standingRepository.save(s);
        }
    }
}

//"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwZ3JheXNvbjAxMUBnbWFpbC5jb20iLCJpYXQiOjE3NzQ1NDM2NzksImV4cCI6MTc3NDYzMDA3OX0.IMTiD0kjpzmdzXq4ZKM7Lx4mEUCDKqQZfmfT11CIRDs"