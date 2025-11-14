package com.fantasyfootball.fantasy_analyzer.controller;

import com.fantasyfootball.fantasy_analyzer.model.dto.sleeper.*;
import com.fantasyfootball.fantasy_analyzer.service.SleeperApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sleeper")
@RequiredArgsConstructor
public class SleeperController {

    private final SleeperApiService sleeperApiService;

    /**
     * GET /api/sleeper/user/{usernameOrId}
     * Busca informações de um usuário do Sleeper
     */
    @GetMapping("/user/{usernameOrId}")
    public ResponseEntity<SleeperUser> getUser(@PathVariable String usernameOrId) {
        SleeperUser user = sleeperApiService.getUser(usernameOrId);
        return ResponseEntity.ok(user);
    }

    /**
     * GET /api/sleeper/user/{userId}/leagues/{sport}/{season}
     * Busca todas as ligas de um usuário em uma temporada
     * Exemplo: /api/sleeper/user/123456/leagues/nfl/2024
     */
    @GetMapping("/user/{userId}/leagues/{sport}/{season}")
    public ResponseEntity<List<SleeperLeague>> getUserLeagues(
            @PathVariable String userId,
            @PathVariable String sport,
            @PathVariable String season) {
        List<SleeperLeague> leagues = sleeperApiService.getUserLeagues(userId, sport, season);
        return ResponseEntity.ok(leagues);
    }

    /**
     * GET /api/sleeper/league/{leagueId}
     * Busca informações detalhadas de uma liga
     */
    @GetMapping("/league/{leagueId}")
    public ResponseEntity<SleeperLeague> getLeague(@PathVariable String leagueId) {
        SleeperLeague league = sleeperApiService.getLeague(leagueId);
        return ResponseEntity.ok(league);
    }

    /**
     * GET /api/sleeper/league/{leagueId}/rosters
     * Busca todos os rosters de uma liga
     */
    @GetMapping("/league/{leagueId}/rosters")
    public ResponseEntity<List<SleeperRoster>> getLeagueRosters(@PathVariable String leagueId) {
        List<SleeperRoster> rosters = sleeperApiService.getLeagueRosters(leagueId);
        return ResponseEntity.ok(rosters);
    }

    /**
     * GET /api/sleeper/league/{leagueId}/users
     * Busca todos os usuários de uma liga
     */
    @GetMapping("/league/{leagueId}/users")
    public ResponseEntity<List<SleeperUser>> getLeagueUsers(@PathVariable String leagueId) {
        List<SleeperUser> users = sleeperApiService.getLeagueUsers(leagueId);
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/sleeper/league/{leagueId}/matchups/{week}
     * Busca os matchups de uma semana específica
     * Exemplo: /api/sleeper/league/123456/matchups/1
     */
    @GetMapping("/league/{leagueId}/matchups/{week}")
    public ResponseEntity<List<SleeperMatchup>> getLeagueMatchups(
            @PathVariable String leagueId,
            @PathVariable Integer week) {
        List<SleeperMatchup> matchups = sleeperApiService.getLeagueMatchups(leagueId, week);
        return ResponseEntity.ok(matchups);
    }

    /**
     * GET /api/sleeper/players/{sport}
     * Busca todos os jogadores disponíveis no Sleeper para um esporte
     * Exemplo: /api/sleeper/players/nfl
     */
    @GetMapping("/players/{sport}")
    public ResponseEntity<Map<String, SleeperPlayer>> getAllPlayers(@PathVariable String sport) {
        Map<String, SleeperPlayer> players = sleeperApiService.getAllPlayers(sport);
        return ResponseEntity.ok(players);
    }

    /**
     * GET /api/sleeper/players/{sport}/trending/{type}
     * Busca jogadores em alta (trending)
     * type: add (adicionados) ou drop (dropados)
     * Parâmetros opcionais:
     * - lookbackHours: quantas horas analisar (padrão: 24)
     * - limit: limite de resultados (padrão: 25)
     *
     * Exemplo: /api/sleeper/players/nfl/trending/add?lookbackHours=48&limit=10
     */
    @GetMapping("/players/{sport}/trending/{type}")
    public ResponseEntity<List<Map<String, Object>>> getTrendingPlayers(
            @PathVariable String sport,
            @PathVariable String type,
            @RequestParam(required = false, defaultValue = "24") Integer lookbackHours,
            @RequestParam(required = false, defaultValue = "25") Integer limit) {
        List<Map<String, Object>> trending = sleeperApiService.getTrendingPlayers(sport, type, lookbackHours, limit);
        return ResponseEntity.ok(trending);
    }
}
