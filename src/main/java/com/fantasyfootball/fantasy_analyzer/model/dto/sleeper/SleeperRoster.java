package com.fantasyfootball.fantasy_analyzer.model.dto.sleeper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SleeperRoster {

    @JsonProperty("roster_id")
    private Integer rosterId;

    @JsonProperty("owner_id")
    private String ownerId;

    private List<String> players;

    private List<String> starters;

    private List<String> reserve;

    private Map<String, Object> settings;

    @JsonProperty("league_id")
    private String leagueId;

    @JsonProperty("player_map")
    private Map<String, Object> playerMap;
}
