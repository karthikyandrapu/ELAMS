package com.elams.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long empId;
    private String userName;
    private String password;
    private String role;
    private boolean isAccountLocked;

    // Lombok's @Data provides getters, setters, equals, hashCode, and toString
    // @Builder allows for easy object creation using the builder pattern
    // @NoArgsConstructor creates a constructor with no arguments
    // @AllArgsConstructor creates a constructor with arguments for all fields
}