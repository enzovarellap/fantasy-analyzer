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
public class SleeperPlayer {

    @JsonProperty("player_id")
    private String playerId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("full_name")
    private String fullName;

    private String position;

    private String team;

    private Integer number;

    private Integer age;

    private String status;

    @JsonProperty("fantasy_positions")
    private List<String> fantasyPositions;

    @JsonProperty("years_exp")
    private Integer yearsExp;

    private Map<String, Object> metadata;
}
