package com.windlabs.cms.domain.Service;

import com.windlabs.cms.common.exception.BadRequestException;
import com.windlabs.cms.common.exception.ResourceNotFoundException;
import com.windlabs.cms.domain.DTO.CreateDomainRequest;
import com.windlabs.cms.domain.DTO.DomainResponse;
import com.windlabs.cms.domain.DTO.UpdateDomainRequest;
import com.windlabs.cms.domain.Entity.DomainEntity;
import com.windlabs.cms.domain.Enum.DomainStatus;
import com.windlabs.cms.domain.Repository.DomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.windlabs.cms.common.response.PageResponse;
import com.windlabs.cms.common.response.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DomainService {

    private final DomainRepository domainRepository;

    @Transactional
    public DomainResponse createDomain(CreateDomainRequest request) {
        String normalizedHost = normalizeHost(request.getHost());

        if (domainRepository.existsByHostIgnoreCase(normalizedHost)) {
            throw new BadRequestException("Domain host already exists: " + normalizedHost);
        }

        DomainEntity domain = DomainEntity.builder()
                .name(request.getName().trim())
                .host(normalizedHost)
                .status(request.getStatus() != null ? request.getStatus() : DomainStatus.ACTIVE)
                .defaultLocale(
                        request.getDefaultLocale() != null && !request.getDefaultLocale().isBlank()
                                ? request.getDefaultLocale().trim()
                                : "id-ID"
                )
                .themeKey(
                        request.getThemeKey() != null && !request.getThemeKey().isBlank()
                                ? request.getThemeKey().trim()
                                : "default"
                )
                .build();

        DomainEntity savedDomain = domainRepository.save(domain);

        return toResponse(savedDomain);
    }

   @Transactional(readOnly = true)
    public PageResponse<DomainResponse> getAllDomains(int page, int size) {
        Pageable pageable = PaginationUtil.createPageable(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    
        Page<DomainEntity> domainPage = domainRepository.findAll(pageable);
    
        List<DomainResponse> items = domainPage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();
    
        return PageResponse.of(domainPage, items);
    }

    @Transactional(readOnly = true)
    public DomainResponse getDomainById(UUID id) {
        DomainEntity domain = findDomainOrThrow(id);

        return toResponse(domain);
    }

    @Transactional(readOnly = true)
    public DomainResponse getDomainByHost(String host) {
        String normalizedHost = normalizeHost(host);

        DomainEntity domain = domainRepository.findByHostIgnoreCase(normalizedHost)
                .orElseThrow(() -> new ResourceNotFoundException("Domain not found with host: " + normalizedHost));

        return toResponse(domain);
    }

    @Transactional
    public DomainResponse updateDomain(UUID id, UpdateDomainRequest request) {
        DomainEntity domain = findDomainOrThrow(id);

        if (request.getName() != null && !request.getName().isBlank()) {
            domain.setName(request.getName().trim());
        }

        if (request.getHost() != null && !request.getHost().isBlank()) {
            String normalizedHost = normalizeHost(request.getHost());

            domainRepository.findByHostIgnoreCase(normalizedHost)
                    .ifPresent(existingDomain -> {
                        if (!existingDomain.getId().equals(id)) {
                            throw new BadRequestException("Domain host already exists: " + normalizedHost);
                        }
                    });

            domain.setHost(normalizedHost);
        }

        if (request.getStatus() != null) {
            domain.setStatus(request.getStatus());
        }

        if (request.getDefaultLocale() != null && !request.getDefaultLocale().isBlank()) {
            domain.setDefaultLocale(request.getDefaultLocale().trim());
        }

        if (request.getThemeKey() != null && !request.getThemeKey().isBlank()) {
            domain.setThemeKey(request.getThemeKey().trim());
        }

        DomainEntity updatedDomain = domainRepository.save(domain);

        return toResponse(updatedDomain);
    }

    @Transactional
    public DomainResponse activateDomain(UUID id) {
        DomainEntity domain = findDomainOrThrow(id);
        domain.setStatus(DomainStatus.ACTIVE);

        return toResponse(domainRepository.save(domain));
    }

    @Transactional
    public DomainResponse deactivateDomain(UUID id) {
        DomainEntity domain = findDomainOrThrow(id);
        domain.setStatus(DomainStatus.INACTIVE);

        return toResponse(domainRepository.save(domain));
    }

    @Transactional
    public DomainResponse suspendDomain(UUID id) {
        DomainEntity domain = findDomainOrThrow(id);
        domain.setStatus(DomainStatus.SUSPENDED);

        return toResponse(domainRepository.save(domain));
    }

    @Transactional
    public void deleteDomain(UUID id) {
        DomainEntity domain = findDomainOrThrow(id);

        domainRepository.delete(domain);
    }

    private DomainEntity findDomainOrThrow(UUID id) {
        return domainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Domain not found with id: " + id));
    }

    private DomainResponse toResponse(DomainEntity domain) {
        return DomainResponse.builder()
                .id(domain.getId())
                .name(domain.getName())
                .host(domain.getHost())
                .status(domain.getStatus())
                .defaultLocale(domain.getDefaultLocale())
                .themeKey(domain.getThemeKey())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private String normalizeHost(String host) {
        if (host == null || host.isBlank()) {
            throw new BadRequestException("Host is required");
        }

        return host
                .trim()
                .toLowerCase()
                .replace("https://", "")
                .replace("http://", "")
                .replace("www.", "")
                .replaceAll("/$", "");
    }
}