package com.suhsaechan.dongbanza.common.config;

import com.suhsaechan.dongbanza.common.properties.SwaggerProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

@OpenAPIDefinition(
    info = @Info(
        title = "그녀를 살려라",
        summary = "그녀가 사다준 뉴욕 베이글 (그뉴베)",
        description = "2024년 05월 17일 - 해커톤",
        version = "0.1v"
    )
)
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final SwaggerProperties swaggerProperties;

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme apiKey = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .in(SecurityScheme.In.HEADER)
            .name("Authorization")
            .scheme("bearer")
            .bearerFormat("JWT");

        SecurityRequirement securityRequirement = new SecurityRequirement()
            .addList("Bearer Token");

        return new OpenAPI()
            .components(new Components().addSecuritySchemes("Bearer Token", apiKey))
            .addSecurityItem(securityRequirement)
            .servers(
                swaggerProperties.getServers().stream()
                    .map(server -> new Server()
                        .url(server.getUrl())
                        .description(server.getDescription()))
                    .collect(Collectors.toList())
            );
    }
}