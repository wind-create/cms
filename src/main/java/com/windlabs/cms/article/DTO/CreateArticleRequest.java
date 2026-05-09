package com.windlabs.cms.article.DTO;

import com.windlabs.cms.article.Enum.ArticleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateArticleRequest {

    @NotNull(message = "Domain id is required")
    private UUID domainId;

    private UUID authorId;

    private UUID categoryId;

    @NotBlank(message = "Article title is required")
    @Size(max = 255, message = "Article title must not exceed 255 characters")
    private String title;

    @Size(max = 255, message = "Slug must not exceed 255 characters")
    private String slug;

    private String excerpt;

    @NotBlank(message = "Article content is required")
    private String content;

    private ArticleStatus status;

    @Size(max = 255, message = "SEO title must not exceed 255 characters")
    private String seoTitle;

    private String seoDescription;

    private String canonicalUrl;

    private String featuredImageUrl;

    private LocalDateTime scheduledAt;

    private UUID createdBy;
}