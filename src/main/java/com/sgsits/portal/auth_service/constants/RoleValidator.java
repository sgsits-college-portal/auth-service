package com.sgsits.portal.auth_service.constants;


import java.util.Arrays;
import java.util.List;

public class RoleValidator {

    private static final List<String> VALID_ROLES = Arrays.asList(
            RoleConstants.ROLE_STUDENT, RoleConstants.ROLE_FACULTY,
            RoleConstants.ROLE_HOD, RoleConstants.ROLE_STAFF, RoleConstants.ROLE_ADMIN
    );

    private static final List<String> VALID_SUB_ROLES = Arrays.asList(
            RoleConstants.SUB_NONE, RoleConstants.SUB_CR, RoleConstants.SUB_PROFESSOR,
            RoleConstants.SUB_HEAD_OF_DEPT, RoleConstants.SUB_LAB_INCHARGE,
            RoleConstants.SUB_LIBRARIAN, RoleConstants.SUB_TECHNICIAN,
            RoleConstants.SUB_OFFICE_ADMIN, RoleConstants.SUB_SYSTEM_ADMIN,
            RoleConstants.SUB_SUPER_ADMIN
    );

    public static boolean isValidRole(String role) {
        return role != null && VALID_ROLES.contains(role.toUpperCase());
    }

    public static boolean isValidSubRole(String subRole) {
        // SubRole can be null or empty if it's the "NONE" sub-role
        return subRole == null || VALID_SUB_ROLES.contains(subRole.toUpperCase());
    }
}