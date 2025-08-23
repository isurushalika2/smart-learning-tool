package org.learningtool.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for LearningTool.
 *
 * Helpful URLs when the application is running (default port 8080):
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 *   Alternative UI entry: http://localhost:8080/swagger-ui/index.html
 * - OpenAPI JSON (default group): http://localhost:8080/v3/api-docs
 * - OpenAPI JSON ("learningtool" group): http://localhost:8080/v3/api-docs/learningtool
 * - OpenAPI YAML (default group): http://localhost:8080/v3/api-docs.yaml
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "LearningTool API",
                version = "0.1.0",
                description = "REST API for content generation and DynamoDB-backed history storage.",
                contact = @Contact(name = "LearningTool", email = "support@example.com"),
                license = @License(name = "Apache-2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")
        )
)
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi apiGroup() {
        return GroupedOpenApi.builder()
                .group("learningtool")
                .pathsToMatch("/api/**")
                .build();
    }
}
