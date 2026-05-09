package com.windlabs.cms.category.Controller;

import com.windlabs.cms.category.DTO.CategoryResponse;
import com.windlabs.cms.category.DTO.CreateCategoryRequest;
import com.windlabs.cms.category.DTO.UpdateCategoryRequest;
import com.windlabs.cms.category.Service.CategoryService;
import com.windlabs.cms.common.response.ApiResponse;
import com.windlabs.cms.common.response.PageResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${app.api.base-path}/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        CategoryResponse content = categoryService.createCategory(request);

        ApiResponse<CategoryResponse> response = ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Category created successfully",
                content
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CategoryResponse>>> getAllCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<CategoryResponse> content = categoryService.getAllCategories(page, size);
    
        ApiResponse<PageResponse<CategoryResponse>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Categories retrieved successfully",
                content
        );
    
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @PathVariable UUID id
    ) {
        CategoryResponse content = categoryService.getCategoryById(id);

        ApiResponse<CategoryResponse> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Category retrieved successfully",
                content
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/domain/{domainId}")
    public ResponseEntity<ApiResponse<PageResponse<CategoryResponse>>> getCategoriesByDomainId(
            @PathVariable UUID domainId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<CategoryResponse> content = categoryService.getCategoriesByDomainId(domainId, page, size);
    
        ApiResponse<PageResponse<CategoryResponse>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Categories by domain retrieved successfully",
                content
        );
    
        return ResponseEntity.ok(response);
    }

    @GetMapping("/domain/{domainId}/slug/{slug}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryByDomainAndSlug(
            @PathVariable UUID domainId,
            @PathVariable String slug
    ) {
        CategoryResponse content = categoryService.getCategoryByDomainAndSlug(domainId, slug);

        ApiResponse<CategoryResponse> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Category retrieved successfully",
                content
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequest request
    ) {
        CategoryResponse content = categoryService.updateCategory(id, request);

        ApiResponse<CategoryResponse> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Category updated successfully",
                content
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteCategory(
            @PathVariable UUID id
    ) {
        categoryService.deleteCategory(id);

        ApiResponse<Object> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Category deleted successfully",
                null
        );

        return ResponseEntity.ok(response);
    }
}