package org.learningtool.config;

import org.learningtool.repository.GenerationHistoryRepository;
import org.learningtool.repository.InMemoryGenerationHistoryRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
        // Allow requests from local Vite dev server
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    // Fallback bean: ensure a GenerationHistoryRepository exists when Mongo is not configured
    @Bean
    @ConditionalOnMissingBean(GenerationHistoryRepository.class)
    public GenerationHistoryRepository generationHistoryRepository() {
        return new InMemoryGenerationHistoryRepository();
    }
}
