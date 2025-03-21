package com.elams.entities;


public enum LeaveType {
    SICK(5.0),
    VACATION(8.0),
    CASUAL(3.0),
    OTHER(3.0);

    private final Double defaultLeaves;

    LeaveType(Double defaultLeaves) {
        this.defaultLeaves = defaultLeaves;
    }

    public Double getDefaultLeaves() {
        return defaultLeaves;
    }
}