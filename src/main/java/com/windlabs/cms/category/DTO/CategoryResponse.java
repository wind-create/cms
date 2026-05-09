package com.windlabs.cms.category.DTO;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CategoryResponse {

    private UUID id;

    private UUID domainId;

    private String domainName;

    private String domainHost;

    private String name;

    private String slug;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}