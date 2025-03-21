package com.elams.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import jakarta.validation.Validator;

/**
 * Configuration class for setting up a custom Validator bean.
 * This class provides a Validator instance for use in request validation.
 */
@Configuration
public class ValidationConfig {

    /**
     * Creates and configures a Validator bean.
     * This bean is used for validating request payloads.
     *
     * @return A configured Validator instance.
     */
    @Bean
    public Validator validator() {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.afterPropertiesSet();
        return factory;
    }
}
