package com.fantasyfootball.fantasy_analyzer.model.dto.sleeper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SleeperUser {

    @JsonProperty("user_id")
    private String userId;

    private String username;

    @JsonProperty("display_name")
    private String displayName;

    private String avatar;

    @JsonProperty("is_bot")
    private Boolean isBot;
}
