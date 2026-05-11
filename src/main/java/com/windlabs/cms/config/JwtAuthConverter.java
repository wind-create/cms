package com.windlabs.cms.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        List<String> roles = getStringListClaim(jwt, "roles");
        List<String> permissions = getStringListClaim(jwt, "permissions");

        Collection<SimpleGrantedAuthority> authorities = Stream.concat(
                        roles.stream().map(role -> "ROLE_" + role),
                        permissions.stream()
                )
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }

    private List<String> getStringListClaim(Jwt jwt, String claimName) {
        List<String> claim = jwt.getClaimAsStringList(claimName);
        return claim == null ? List.of() : claim;
    }
}