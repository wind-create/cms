package com.windlabs.cms.article.Controller;

import com.windlabs.cms.article.DTO.PublicArticleDetailResponse;
import com.windlabs.cms.article.DTO.PublicArticleListResponse;
import com.windlabs.cms.article.Service.PublicArticleService;
import com.windlabs.cms.common.response.ApiResponse;
import com.windlabs.cms.common.response.PageResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${app.api.base-path}/public")
@RequiredArgsConstructor
public class PublicArticleController {

    private final PublicArticleService publicArticleService;

    @GetMapping("/{host:.+}/articles")
    public ResponseEntity<ApiResponse<PageResponse<PublicArticleListResponse>>> getPublishedArticlesByHost(
            @PathVariable String host,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<PublicArticleListResponse> content =
                publicArticleService.getPublishedArticlesByHost(host, page, size);
    
        ApiResponse<PageResponse<PublicArticleListResponse>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Published articles retrieved successfully",
                content
        );
    
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{host:.+}/article/{slug}")
    public ResponseEntity<ApiResponse<PublicArticleDetailResponse>> getPublishedArticleByHostAndSlug(
            @PathVariable String host,
            @PathVariable String slug
    ) {
        PublicArticleDetailResponse content =
                publicArticleService.getPublishedArticleByHostAndSlug(host, slug);

        ApiResponse<PublicArticleDetailResponse> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Published article retrieved successfully",
                content
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{host:.+}/category/{slug}")
    public ResponseEntity<ApiResponse<PageResponse<PublicArticleListResponse>>> getPublishedArticlesByCategory(
            @PathVariable String host,
            @PathVariable String slug,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<PublicArticleListResponse> content =
                publicArticleService.getPublishedArticlesByHostAndCategorySlug(host, slug, page, size);
    
        ApiResponse<PageResponse<PublicArticleListResponse>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Published articles by category retrieved successfully",
                content
        );
    
        return ResponseEntity.ok(response);
    }
}