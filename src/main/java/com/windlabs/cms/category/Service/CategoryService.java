package com.windlabs.cms.category.Service;

import com.windlabs.cms.category.DTO.CategoryResponse;
import com.windlabs.cms.category.DTO.CreateCategoryRequest;
import com.windlabs.cms.category.DTO.UpdateCategoryRequest;
import com.windlabs.cms.category.Entity.CategoryEntity;
import com.windlabs.cms.category.Repository.CategoryRepository;
import com.windlabs.cms.common.exception.BadRequestException;
import com.windlabs.cms.common.exception.ResourceNotFoundException;
import com.windlabs.cms.common.response.PageResponse;
import com.windlabs.cms.common.response.PaginationUtil;
import com.windlabs.cms.domain.Entity.DomainEntity;
import com.windlabs.cms.domain.Repository.DomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final DomainRepository domainRepository;

    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        DomainEntity domain = findDomainOrThrow(request.getDomainId());

        String slug = resolveSlug(request.getSlug(), request.getName());

        if (categoryRepository.existsByDomain_IdAndSlugIgnoreCase(domain.getId(), slug)) {
            throw new BadRequestException("Category slug already exists in this domain: " + slug);
        }

        CategoryEntity category = CategoryEntity.builder()
                .domain(domain)
                .name(request.getName().trim())
                .slug(slug)
                .description(normalizeNullableText(request.getDescription()))
                .build();

        CategoryEntity savedCategory = categoryRepository.save(category);

        return toResponse(savedCategory);
    }

    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> getAllCategories(int page, int size) {
        Pageable pageable = PaginationUtil.createPageable(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    
        Page<CategoryEntity> categoryPage = categoryRepository.findAll(pageable);
    
        List<CategoryResponse> items = categoryPage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();
    
        return PageResponse.of(categoryPage, items);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(UUID id) {
        CategoryEntity category = findCategoryOrThrow(id);

        return toResponse(category);
    }

    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> getCategoriesByDomainId(UUID domainId, int page, int size) {
        findDomainOrThrow(domainId);
    
        Pageable pageable = PaginationUtil.createPageable(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "name")
        );
    
        Page<CategoryEntity> categoryPage = categoryRepository.findByDomain_Id(domainId, pageable);
    
        List<CategoryResponse> items = categoryPage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();
    
        return PageResponse.of(categoryPage, items);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryByDomainAndSlug(UUID domainId, String slug) {
        findDomainOrThrow(domainId);

        String normalizedSlug = slugify(slug);

        CategoryEntity category = categoryRepository
                .findByDomain_IdAndSlugIgnoreCase(domainId, normalizedSlug)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with slug: " + normalizedSlug + " in domain id: " + domainId
                ));

        return toResponse(category);
    }

    @Transactional
    public CategoryResponse updateCategory(UUID id, UpdateCategoryRequest request) {
        CategoryEntity category = findCategoryOrThrow(id);

        DomainEntity targetDomain = category.getDomain();

        if (request.getDomainId() != null) {
            targetDomain = findDomainOrThrow(request.getDomainId());
            category.setDomain(targetDomain);
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            category.setName(request.getName().trim());
        }

        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            String normalizedSlug = slugify(request.getSlug());

            validateUniqueSlugForUpdate(
                    category.getId(),
                    targetDomain.getId(),
                    normalizedSlug
            );

            category.setSlug(normalizedSlug);
        } else if (request.getDomainId() != null) {
            validateUniqueSlugForUpdate(
                    category.getId(),
                    targetDomain.getId(),
                    category.getSlug()
            );
        }

        if (request.getDescription() != null) {
            category.setDescription(normalizeNullableText(request.getDescription()));
        }

        CategoryEntity updatedCategory = categoryRepository.save(category);

        return toResponse(updatedCategory);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        CategoryEntity category = findCategoryOrThrow(id);

        categoryRepository.delete(category);
    }

    private void validateUniqueSlugForUpdate(UUID currentCategoryId, UUID domainId, String slug) {
        categoryRepository.findByDomain_IdAndSlugIgnoreCase(domainId, slug)
                .ifPresent(existingCategory -> {
                    if (!existingCategory.getId().equals(currentCategoryId)) {
                        throw new BadRequestException("Category slug already exists in this domain: " + slug);
                    }
                });
    }

    private CategoryEntity findCategoryOrThrow(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private DomainEntity findDomainOrThrow(UUID domainId) {
        return domainRepository.findById(domainId)
                .orElseThrow(() -> new ResourceNotFoundException("Domain not found with id: " + domainId));
    }

    private CategoryResponse toResponse(CategoryEntity category) {
        DomainEntity domain = category.getDomain();

        return CategoryResponse.builder()
                .id(category.getId())
                .domainId(domain.getId())
                .domainName(domain.getName())
                .domainHost(domain.getHost())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    private String resolveSlug(String requestSlug, String name) {
        if (requestSlug != null && !requestSlug.isBlank()) {
            return slugify(requestSlug);
        }

        return slugify(name);
    }

    private String slugify(String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("Slug source value is required");
        }

        String normalized = Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        String slug = normalized
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        if (slug.isBlank()) {
            throw new BadRequestException("Invalid slug value");
        }

        return slug;
    }

    private String normalizeNullableText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}