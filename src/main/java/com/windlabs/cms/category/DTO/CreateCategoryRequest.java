package com.windlabs.cms.category.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateCategoryRequest {

    @NotNull(message = "Domain id is required")
    private UUID domainId;

    @NotBlank(message = "Category name is required")
    @Size(max = 150, message = "Category name must not exceed 150 characters")
    private String name;

    @Size(max = 180, message = "Slug must not exceed 180 characters")
    private String slug;

    private String description;
}