package com.windlabs.cms.article.DTO;

import com.windlabs.cms.article.Enum.ArticleStatus;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UpdateArticleRequest {

    private UUID domainId;

    private UUID authorId;

    private UUID categoryId;

    @Size(max = 255, message = "Article title must not exceed 255 characters")
    private String title;

    @Size(max = 255, message = "Slug must not exceed 255 characters")
    private String slug;

    private String excerpt;

    private String content;

    private ArticleStatus status;

    @Size(max = 255, message = "SEO title must not exceed 255 characters")
    private String seoTitle;

    private String seoDescription;

    private String canonicalUrl;

    private String featuredImageUrl;

    private LocalDateTime scheduledAt;

    private UUID updatedBy;
}