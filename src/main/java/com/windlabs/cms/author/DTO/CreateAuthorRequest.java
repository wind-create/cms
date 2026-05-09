package com.windlabs.cms.author.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAuthorRequest {

    @NotBlank(message = "Author name is required")
    @Size(max = 150, message = "Author name must not exceed 150 characters")
    private String name;

    @Size(max = 180, message = "Slug must not exceed 180 characters")
    private String slug;

    private String bio;

    private String avatarUrl;
}