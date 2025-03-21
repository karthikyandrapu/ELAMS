package com.elams.config;

import com.elams.aop.AppLogger;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Swagger documentation using OpenAPI 3.
 * This class defines a bean to customize the OpenAPI documentation for the Attendance Management API.
 */
@Configuration
public class SwaggerDocumentation {

    private static final Logger logger = AppLogger.getLogger(SwaggerDocumentation.class);

    /**
     * Creates and configures an OpenAPI bean for Swagger documentation.
     * This bean defines the title, description, and version of the API documentation.
     *
     * @return An instance of {@link OpenAPI} with customized information.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        logger.info("Configuring custom OpenAPI documentation.");
        OpenAPI openAPI = new OpenAPI()
                .info(new Info().title("Attendance Management ")
                        .description("API Documentation")
                        .version("v1.0"));
        logger.info("Custom OpenAPI documentation configured successfully.");
        return openAPI;
    }
}