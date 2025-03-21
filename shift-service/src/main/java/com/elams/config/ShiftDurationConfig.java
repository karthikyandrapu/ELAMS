package com.elams.config;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for managing shift duration.
 * This class provides a centralized way to define and access the default shift duration.
 * The shift duration is hardcoded within this class, removing the need for external configuration files.
 */
@Configuration
public class ShiftDurationConfig {

    private final int durationHours;

    /**
     * Constructs a new ShiftDurationConfig with a default shift duration of 9 hours.
     */
    public ShiftDurationConfig() {
        this.durationHours = 9;
    }

    /**
     * Retrieves the configured shift duration in hours.
     *
     * @return The shift duration in hours.
     */
    public int getDurationHours() {
        return durationHours;
    }
}