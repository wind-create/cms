package com.windlabs.cms.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${app.cors.allowed-methods}")
    private String allowedMethods;

    @Value("${app.cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${app.cors.allow-credentials}")
    private boolean allowCredentials;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        // =========================
                        // Swagger / OpenAPI
                        // =========================
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // =========================
                        // Public CMS API
                        // =========================
                        .requestMatchers("/cms/public/**").permitAll()

                        // =========================
                        // Domain Management
                        // =========================
                        .requestMatchers(HttpMethod.GET, "/cms/domain/**").hasAuthority("DOMAIN_READ")
                        .requestMatchers(HttpMethod.POST, "/cms/domain/**").hasAuthority("DOMAIN_CREATE")
                        .requestMatchers(HttpMethod.PUT, "/cms/domain/**").hasAuthority("DOMAIN_UPDATE")
                        .requestMatchers(HttpMethod.PATCH, "/cms/domain/**").hasAuthority("DOMAIN_UPDATE")
                        .requestMatchers(HttpMethod.DELETE, "/cms/domain/**").hasAuthority("DOMAIN_DELETE")

                        // =========================
                        // Author Management
                        // =========================
                        .requestMatchers(HttpMethod.GET, "/cms/author/**").hasAuthority("AUTHOR_READ")
                        .requestMatchers(HttpMethod.POST, "/cms/author/**").hasAuthority("AUTHOR_CREATE")
                        .requestMatchers(HttpMethod.PUT, "/cms/author/**").hasAuthority("AUTHOR_UPDATE")
                        .requestMatchers(HttpMethod.PATCH, "/cms/author/**").hasAuthority("AUTHOR_UPDATE")
                        .requestMatchers(HttpMethod.DELETE, "/cms/author/**").hasAuthority("AUTHOR_DELETE")

                        // =========================
                        // Category Management
                        // =========================
                        .requestMatchers(HttpMethod.GET, "/cms/category/**").hasAuthority("CATEGORY_READ")
                        .requestMatchers(HttpMethod.POST, "/cms/category/**").hasAuthority("CATEGORY_CREATE")
                        .requestMatchers(HttpMethod.PUT, "/cms/category/**").hasAuthority("CATEGORY_UPDATE")
                        .requestMatchers(HttpMethod.PATCH, "/cms/category/**").hasAuthority("CATEGORY_UPDATE")
                        .requestMatchers(HttpMethod.DELETE, "/cms/category/**").hasAuthority("CATEGORY_DELETE")

                        // =========================
                        // Article Workflow
                        // Put publish/unpublish before generic POST rule.
                        // =========================
                        .requestMatchers(HttpMethod.POST, "/cms/article/*/publish").hasAuthority("ARTICLE_PUBLISH")
                        .requestMatchers(HttpMethod.POST, "/cms/article/*/unpublish").hasAuthority("ARTICLE_UNPUBLISH")

                        .requestMatchers(HttpMethod.GET, "/cms/article/**").hasAuthority("ARTICLE_READ")
                        .requestMatchers(HttpMethod.POST, "/cms/article/**").hasAuthority("ARTICLE_CREATE")
                        .requestMatchers(HttpMethod.PUT, "/cms/article/**").hasAuthority("ARTICLE_UPDATE")
                        .requestMatchers(HttpMethod.PATCH, "/cms/article/**").hasAuthority("ARTICLE_UPDATE")
                        .requestMatchers(HttpMethod.DELETE, "/cms/article/**").hasAuthority("ARTICLE_DELETE")

                        // =========================
                        // Media
                        // =========================
                        .requestMatchers(HttpMethod.GET, "/cms/media/**").hasAuthority("MEDIA_READ")
                        .requestMatchers(HttpMethod.POST, "/cms/media/**").hasAuthority("MEDIA_UPLOAD")
                        .requestMatchers(HttpMethod.PUT, "/cms/media/**").hasAuthority("MEDIA_UPDATE")
                        .requestMatchers(HttpMethod.PATCH, "/cms/media/**").hasAuthority("MEDIA_UPDATE")
                        .requestMatchers(HttpMethod.DELETE, "/cms/media/**").hasAuthority("MEDIA_DELETE")

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter))
                )
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey key = new SecretKeySpec(
                jwtSecret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        OAuth2TokenValidator<Jwt> validator = JwtValidators.createDefaultWithIssuer(issuer);
        decoder.setJwtValidator(validator);

        return decoder;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(splitProperty(allowedOrigins));
        config.setAllowedMethods(splitProperty(allowedMethods));
        config.setAllowedHeaders(splitProperty(allowedHeaders));
        config.setAllowCredentials(allowCredentials);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    private List<String> splitProperty(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList();
    }
}