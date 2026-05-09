package com.windlabs.cms.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private Boolean success;

    private Integer status;

    private String message;

    private T content;

    private Object errors;

    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(Integer status, String message, T content) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(status)
                .message(message)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ApiResponse<Object> error(Integer status, String message, Object errors) {
        return ApiResponse.builder()
                .success(false)
                .status(status)
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}