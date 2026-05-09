package com.windlabs.cms.article.DTO;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PublicArticleListResponse {

    private UUID id;

    private String title;

    private String slug;

    private String excerpt;

    private String featuredImageUrl;

    private String seoTitle;

    private String seoDescription;

    private LocalDateTime publishedAt;

    private String authorName;

    private String authorSlug;

    private String categoryName;

    private String categorySlug;
}