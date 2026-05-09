package com.windlabs.cms.article.DTO;

import com.windlabs.cms.article.Enum.ArticleStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ArticleResponse {

    private UUID id;

    private UUID domainId;
    private String domainName;
    private String domainHost;

    private UUID authorId;
    private String authorName;
    private String authorSlug;

    private UUID categoryId;
    private String categoryName;
    private String categorySlug;

    private String title;
    private String slug;
    private String excerpt;
    private String content;

    private ArticleStatus status;

    private String seoTitle;
    private String seoDescription;
    private String canonicalUrl;
    private String featuredImageUrl;

    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;

    private UUID createdBy;
    private UUID updatedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}