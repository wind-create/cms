package com.windlabs.cms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.windlabs.cms.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("path", request.getRequestURI());
        error.put("method", request.getMethod());
        error.put("detail", accessDeniedException.getMessage());

        ApiResponse<Object> apiResponse = ApiResponse.error(
                HttpStatus.FORBIDDEN.value(),
                "Access denied",
                error
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}