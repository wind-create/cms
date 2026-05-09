package com.windlabs.cms.domain.DTO;

import com.windlabs.cms.domain.Enum.DomainStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class DomainResponse {

    private UUID id;

    private String name;

    private String host;

    private DomainStatus status;

    private String defaultLocale;

    private String themeKey;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}