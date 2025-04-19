package com.elams.configuration;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for creating and providing a RestTemplate bean.
 * This bean is used for making HTTP requests to external services.
 */
@Configuration
public class RestTemplateConfiguration {

    /**
     * Creates and returns a RestTemplate bean.
     * This bean is used for making HTTP requests.
     *
     * @return A new instance of RestTemplate.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
