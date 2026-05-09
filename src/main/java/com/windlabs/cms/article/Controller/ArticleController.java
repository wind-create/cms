package com.windlabs.cms.article.Controller;

import com.windlabs.cms.article.DTO.ArticleResponse;
import com.windlabs.cms.article.DTO.CreateArticleRequest;
import com.windlabs.cms.article.DTO.UpdateArticleRequest;
import com.windlabs.cms.article.Service.ArticleService;
import com.windlabs.cms.common.response.ApiResponse;
import com.windlabs.cms.common.response.PageResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${app.api.base-path}/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<ApiResponse<ArticleResponse>> createArticle(
            @Valid @RequestBody CreateArticleRequest request
    ) {
        ArticleResponse content = articleService.createArticle(request);

        ApiResponse<ArticleResponse> response = ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Article created successfully",
                content
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ArticleResponse>>> getAllArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ArticleResponse> content = articleService.getAllArticles(page, size);
    
        ApiResponse<PageResponse<ArticleResponse>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Articles retrieved successfully",
                content
        );
    
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleResponse>> getArticleById(
            @PathVariable UUID id
    ) {
        ArticleResponse content = articleService.getArticleById(id);

        ApiResponse<ArticleResponse> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Article retrieved successfully",
                content
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/domain/{domainId}")
    public ResponseEntity<ApiResponse<PageResponse<ArticleResponse>>> getArticlesByDomainId(
            @PathVariable UUID domainId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ArticleResponse> content = articleService.getArticlesByDomainId(domainId, page, size);
    
        ApiResponse<PageResponse<ArticleResponse>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Articles by domain retrieved successfully",
                content
        );
    
        return ResponseEntity.ok(response);
    }

    @GetMapping("/domain/{domainId}/slug/{slug}")
    public ResponseEntity<ApiResponse<ArticleResponse>> getArticleByDomainAndSlug(
            @PathVariable UUID domainId,
            @PathVariable String slug
    ) {
        ArticleResponse content = articleService.getArticleByDomainAndSlug(domainId, slug);

        ApiResponse<ArticleResponse> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Article retrieved successfully",
                content
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleResponse>> updateArticle(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateArticleRequest request
    ) {
        ArticleResponse content = articleService.updateArticle(id, request);

        ApiResponse<ArticleResponse> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Article updated successfully",
                content
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<ArticleResponse>> publishArticle(
            @PathVariable UUID id
    ) {
        ArticleResponse content = articleService.publishArticle(id);

        ApiResponse<ArticleResponse> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Article published successfully",
                content
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/unpublish")
    public ResponseEntity<ApiResponse<ArticleResponse>> unpublishArticle(
            @PathVariable UUID id
    ) {
        ArticleResponse content = articleService.unpublishArticle(id);

        ApiResponse<ArticleResponse> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Article unpublished successfully",
                content
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<ApiResponse<ArticleResponse>> archiveArticle(
            @PathVariable UUID id
    ) {
        ArticleResponse content = articleService.archiveArticle(id);

        ApiResponse<ArticleResponse> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Article archived successfully",
                content
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteArticle(
            @PathVariable UUID id
    ) {
        articleService.deleteArticle(id);

        ApiResponse<Object> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Article deleted successfully",
                null
        );

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/author/{authorId}")
    public ResponseEntity<ApiResponse<PageResponse<ArticleResponse>>> getArticlesByAuthorId(
            @PathVariable UUID authorId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ArticleResponse> content = articleService.getArticlesByAuthorId(authorId, page, size);
    
        ApiResponse<PageResponse<ArticleResponse>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Articles by author retrieved successfully",
                content
        );
    
        return ResponseEntity.ok(response);
    }
}