package com.authms.model;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private String username;

    public JwtResponse(String token,String refreshToken, String username) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.username = username;
    }

}
