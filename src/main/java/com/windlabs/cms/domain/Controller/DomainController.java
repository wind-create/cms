package com.windlabs.cms.domain.Controller;

import com.windlabs.cms.common.response.ApiResponse;
import com.windlabs.cms.domain.DTO.CreateDomainRequest;
import com.windlabs.cms.domain.DTO.DomainResponse;
import com.windlabs.cms.domain.DTO.UpdateDomainRequest;
import com.windlabs.cms.domain.Service.DomainService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.windlabs.cms.common.response.PageResponse;

import java.util.UUID;

@RestController
@RequestMapping("${app.api.base-path}/domains")
@RequiredArgsConstructor
public class DomainController {

    private final DomainService domainService;

    @PostMapping
    public ResponseEntity<ApiResponse<DomainResponse>> createDomain(
            @Valid @RequestBody CreateDomainRequest request
    ) {
        DomainResponse content = domainService.createDomain(request);

        ApiResponse<DomainResponse> response = ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Domain created successfully",
                content
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DomainResponse>>> getAllDomains(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<DomainResponse> content = domainService.getAllDomains(page, size);
    
        ApiResponse<PageResponse<DomainResponse>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Domains retrieved successfully",
                content
        );
    
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DomainResponse>> getDomainById(
            @PathVariable UUID id
    ) {
        DomainResponse content = domainService.getDomainById(id);

        ApiResponse<DomainResponse> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Domain retrieved successfully",
                content
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/host/{host}")
    public ResponseEntity<ApiResponse<DomainResponse>> getDomainByHost(
            @PathVariable String host
    ) {
        DomainResponse content = domainService.getDomainByHost(host);

        ApiResponse<DomainResponse> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Domain retrieved successfully",
                content
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DomainResponse>> updateDomain(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDomainRequest request
    ) {
        DomainResponse content = domainService.updateDomain(id, request);

        ApiResponse<DomainResponse> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Domain updated successfully",
                content
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<DomainResponse>> activateDomain(
            @PathVariable UUID id
    ) {
        DomainResponse content = domainService.activateDomain(id);

        ApiResponse<DomainResponse> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Domain activated successfully",
                content
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<DomainResponse>> deactivateDomain(
            @PathVariable UUID id
    ) {
        DomainResponse content = domainService.deactivateDomain(id);

        ApiResponse<DomainResponse> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Domain deactivated successfully",
                content
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/suspend")
    public ResponseEntity<ApiResponse<DomainResponse>> suspendDomain(
            @PathVariable UUID id
    ) {
        DomainResponse content = domainService.suspendDomain(id);

        ApiResponse<DomainResponse> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Domain suspended successfully",
                content
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteDomain(
            @PathVariable UUID id
    ) {
        domainService.deleteDomain(id);

        ApiResponse<Object> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Domain deleted successfully",
                null
        );

        return ResponseEntity.ok(response);
    }
}