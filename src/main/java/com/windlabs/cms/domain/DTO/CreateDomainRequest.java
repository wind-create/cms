package com.windlabs.cms.domain.DTO;

import com.windlabs.cms.domain.Enum.DomainStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDomainRequest {

    @NotBlank(message = "Domain name is required")
    @Size(max = 150, message = "Domain name must not exceed 150 characters")
    private String name;

    @NotBlank(message = "Host is required")
    @Size(max = 255, message = "Host must not exceed 255 characters")
    private String host;

    private DomainStatus status;

    private String defaultLocale;

    private String themeKey;
}