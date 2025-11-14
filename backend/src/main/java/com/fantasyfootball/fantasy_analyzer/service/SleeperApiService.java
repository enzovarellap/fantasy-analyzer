package com.fantasyfootball.fantasy_analyzer.service;

import com.fantasyfootball.fantasy_analyzer.model.dto.sleeper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SleeperApiService {

    private static final String BASE_URL = "https://api.sleeper.app/v1";
    private final RestTemplate restTemplate;

    /**
     * Busca informações de um usuário pelo username ou user_id
     * @param usernameOrId Username ou User ID do Sleeper
     * @return Dados do usuário
     */
    public SleeperUser getUser(String usernameOrId) {
        String url = String.format("%s/user/%s", BASE_URL, usernameOrId);
        log.info("Buscando usuário: {}", usernameOrId);

        try {
            return restTemplate.getForObject(url, SleeperUser.class);
        } catch (RestClientException e) {
            log.error("Erro ao buscar usuário {}: {}", usernameOrId, e.getMessage());
            throw new RuntimeException("Erro ao buscar usuário do Sleeper", e);
        }
    }

    /**
     * Busca todas as ligas de um usuário em uma temporada específica
     * @param userId ID do usuário
     * @param sport Esporte (nfl, nba, lcs, etc)
     * @param season Ano da temporada (ex: 2024)
     * @return Lista de ligas
     */
    public List<SleeperLeague> getUserLeagues(String userId, String sport, String season) {
        String url = String.format("%s/user/%s/leagues/%s/%s", BASE_URL, userId, sport, season);
        log.info("Buscando ligas do usuário {} - Sport: {} - Season: {}", userId, sport, season);

        try {
            ResponseEntity<List<SleeperLeague>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SleeperLeague>>() {}
            );
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Erro ao buscar ligas do usuário {}: {}", userId, e.getMessage());
            throw new RuntimeException("Erro ao buscar ligas do usuário", e);
        }
    }

    /**
     * Busca informações detalhadas de uma liga
     * @param leagueId ID da liga
     * @return Dados da liga
     */
    public SleeperLeague getLeague(String leagueId) {
        String url = String.format("%s/league/%s", BASE_URL, leagueId);
        log.info("Buscando liga: {}", leagueId);

        try {
            return restTemplate.getForObject(url, SleeperLeague.class);
        } catch (RestClientException e) {
            log.error("Erro ao buscar liga {}: {}", leagueId, e.getMessage());
            throw new RuntimeException("Erro ao buscar liga do Sleeper", e);
        }
    }

    /**
     * Busca todos os rosters de uma liga
     * @param leagueId ID da liga
     * @return Lista de rosters
     */
    public List<SleeperRoster> getLeagueRosters(String leagueId) {
        String url = String.format("%s/league/%s/rosters", BASE_URL, leagueId);
        log.info("Buscando rosters da liga: {}", leagueId);

        try {
            ResponseEntity<List<SleeperRoster>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SleeperRoster>>() {}
            );
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Erro ao buscar rosters da liga {}: {}", leagueId, e.getMessage());
            throw new RuntimeException("Erro ao buscar rosters da liga", e);
        }
    }

    /**
     * Busca todos os usuários de uma liga
     * @param leagueId ID da liga
     * @return Lista de usuários
     */
    public List<SleeperUser> getLeagueUsers(String leagueId) {
        String url = String.format("%s/league/%s/users", BASE_URL, leagueId);
        log.info("Buscando usuários da liga: {}", leagueId);

        try {
            ResponseEntity<List<SleeperUser>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SleeperUser>>() {}
            );
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Erro ao buscar usuários da liga {}: {}", leagueId, e.getMessage());
            throw new RuntimeException("Erro ao buscar usuários da liga", e);
        }
    }

    /**
     * Busca os matchups de uma semana específica
     * @param leagueId ID da liga
     * @param week Número da semana
     * @return Lista de matchups
     */
    public List<SleeperMatchup> getLeagueMatchups(String leagueId, Integer week) {
        String url = String.format("%s/league/%s/matchups/%d", BASE_URL, leagueId, week);
        log.info("Buscando matchups da liga {} - Semana: {}", leagueId, week);

        try {
            ResponseEntity<List<SleeperMatchup>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SleeperMatchup>>() {}
            );
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Erro ao buscar matchups da liga {} - Semana {}: {}", leagueId, week, e.getMessage());
            throw new RuntimeException("Erro ao buscar matchups da liga", e);
        }
    }

    /**
     * Busca todos os jogadores disponíveis no Sleeper para um esporte
     * @param sport Esporte (nfl, nba, lcs, etc)
     * @return Map com dados dos jogadores (player_id -> dados)
     */
    public Map<String, SleeperPlayer> getAllPlayers(String sport) {
        String url = String.format("%s/players/%s", BASE_URL, sport);
        log.info("Buscando todos os jogadores do esporte: {}", sport);

        try {
            ResponseEntity<Map<String, SleeperPlayer>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, SleeperPlayer>>() {}
            );
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Erro ao buscar jogadores do esporte {}: {}", sport, e.getMessage());
            throw new RuntimeException("Erro ao buscar jogadores", e);
        }
    }

    /**
     * Busca jogadores em alta (trending)
     * @param sport Esporte (nfl, nba, lcs, etc)
     * @param type Tipo de trending (add, drop)
     * @param lookbackHours Horas para análise (padrão: 24h)
     * @param limit Limite de resultados (padrão: 25)
     * @return Lista de player_ids em trending
     */
    public List<Map<String, Object>> getTrendingPlayers(String sport, String type, Integer lookbackHours, Integer limit) {
        String url = String.format("%s/players/%s/trending/%s?lookback_hours=%d&limit=%d",
            BASE_URL, sport, type, lookbackHours != null ? lookbackHours : 24, limit != null ? limit : 25);
        log.info("Buscando jogadores trending - Sport: {} - Type: {}", sport, type);

        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Erro ao buscar jogadores trending {}/{}: {}", sport, type, e.getMessage());
            throw new RuntimeException("Erro ao buscar jogadores trending", e);
        }
    }
}
