package com.windlabs.cms.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CurrentUserProvider {

    public UUID getUserId(Authentication authentication) {
        Jwt jwt = getJwt(authentication);
        return UUID.fromString(jwt.getSubject());
    }

    public String getEmail(Authentication authentication) {
        return getJwt(authentication).getClaimAsString("email");
    }

    public String getUsername(Authentication authentication) {
        return getJwt(authentication).getClaimAsString("username");
    }

    public String getName(Authentication authentication) {
        return getJwt(authentication).getClaimAsString("name");
    }

    public List<String> getRoles(Authentication authentication) {
        List<String> roles = getJwt(authentication).getClaimAsStringList("roles");
        return roles == null ? List.of() : roles;
    }

    public List<String> getPermissions(Authentication authentication) {
        List<String> permissions = getJwt(authentication).getClaimAsStringList("permissions");
        return permissions == null ? List.of() : permissions;
    }

    private Jwt getJwt(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
            throw new RuntimeException("Invalid authentication token");
        }

        return jwtAuthenticationToken.getToken();
    }
}