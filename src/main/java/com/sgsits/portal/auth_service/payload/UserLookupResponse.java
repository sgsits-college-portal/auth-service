package com.sgsits.portal.auth_service.payload;

public record UserLookupResponse(
        Long id,
        String fullName,
        String email,
        String role,
        String subRole
) {}