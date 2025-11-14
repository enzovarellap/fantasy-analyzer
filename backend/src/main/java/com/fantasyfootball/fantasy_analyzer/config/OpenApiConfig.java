package com.fantasyfootball.fantasy_analyzer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation.
 * Access documentation at: /swagger-ui.html or /api-docs
 */
@Configuration
public class OpenApiConfig {

    @Value("${app.version:0.0.1-SNAPSHOT}")
    private String appVersion;

    @Value("${app.name:Fantasy Analyzer}")
    private String appName;

    @Value("${app.description:Fantasy Football Analysis Platform}")
    private String appDescription;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(getInfo())
                .servers(getServers())
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description("JWT authentication token")
                                )
                );
    }

    private Info getInfo() {
        return new Info()
                .title(appName + " API")
                .description(appDescription + " - RESTful API Documentation")
                .version(appVersion)
                .contact(new Contact()
                        .name("Development Team")
                        .email("dev@fantasy-analyzer.com")
                        .url("https://github.com/your-org/fantasy-analyzer"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html"));
    }

    private List<Server> getServers() {
        return List.of(
                new Server()
                        .url("http://localhost:8080")
                        .description("Development server"),
                new Server()
                        .url("https://api.fantasy-analyzer.com")
                        .description("Production server")
        );
    }
}
