package com.E2DShare.filemngt.model;

import lombok.Data;

@Data

public class UserRegistrationRequest {
    private String userId;
    private String username;
    private String password;
}
