package com.windlabs.cms.article.DTO;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PublicArticleDetailResponse {

    private UUID id;

    private String title;

    private String slug;

    private String excerpt;

    private String content;

    private String seoTitle;

    private String seoDescription;

    private String canonicalUrl;

    private String featuredImageUrl;

    private LocalDateTime publishedAt;

    private String domainName;

    private String domainHost;

    private String authorName;

    private String authorSlug;

    private String authorBio;

    private String authorAvatarUrl;

    private String categoryName;

    private String categorySlug;
}