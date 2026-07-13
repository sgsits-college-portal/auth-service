package com.sgsits.portal.auth_service.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String role;
    private String subRole;
    private String fullName;
    private String email;

    public JwtResponse(String accessToken, Long id, String username, String role, String subRole, String fullName, String email) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.role = role;
        this.subRole = subRole;
        this.fullName = fullName;
        this.email = email;
    }
}
