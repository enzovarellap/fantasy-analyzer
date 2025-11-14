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
public class SleeperMatchup {

    @JsonProperty("roster_id")
    private Integer rosterId;

    @JsonProperty("matchup_id")
    private Integer matchupId;

    private Double points;

    @JsonProperty("custom_points")
    private Double customPoints;

    private List<String> starters;

    @JsonProperty("starters_points")
    private List<Double> startersPoints;

    private List<String> players;

    @JsonProperty("players_points")
    private Map<String, Double> playersPoints;
}
