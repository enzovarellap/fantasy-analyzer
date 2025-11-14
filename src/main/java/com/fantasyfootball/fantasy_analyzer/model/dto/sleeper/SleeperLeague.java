package com.fantasyfootball.fantasy_analyzer.model.dto.sleeper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SleeperLeague {

    @JsonProperty("league_id")
    private String leagueId;

    private String name;

    private String avatar;

    private String season;

    @JsonProperty("season_type")
    private String seasonType;

    private String sport;

    private String status;

    @JsonProperty("total_rosters")
    private Integer totalRosters;

    private Map<String, Object> settings;

    @JsonProperty("scoring_settings")
    private Map<String, Object> scoringSettings;

    @JsonProperty("roster_positions")
    private String[] rosterPositions;
}
