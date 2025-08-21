package org.learningtool.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.learningtool.repository.DynamoDbGenerationHistoryRepository;
import org.learningtool.repository.GenerationHistoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // Allow requests from any origin (dev/prod). No credentials are used by this app.
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*") );
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    // DynamoDB-backed repository (enabled when dynamodb.enabled=true)
    @Bean
    @ConditionalOnProperty(name = "dynamodb.enabled", havingValue = "true")
    public GenerationHistoryRepository dynamoGenerationHistoryRepository(
            ObjectMapper mapper,
            @Value("${dynamodb.table:learningtool-generation-history}") String table,
            @Value("${dynamodb.region:}") String region
    ) {
        return new DynamoDbGenerationHistoryRepository(mapper, table, region);
    }
}
