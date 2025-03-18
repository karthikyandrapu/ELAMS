package com.elams.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Configuration class for Swagger documentation.
 * This class defines a bean to customize the OpenAPI documentation.
 */
@Configuration
public class SwaggerDocumentation {
	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("Shift Management ")
				.description("API Documentation")	
				.version("v1.0"));
	}
}