package org.simple.server.entity.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER, ADMIN;

    final String roleName = "ROLE_" + name();

    @Override
    public String getAuthority() {
        return roleName;
    }
}