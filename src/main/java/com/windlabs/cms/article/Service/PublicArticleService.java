package com.windlabs.cms.article.Service;

import com.windlabs.cms.article.DTO.PublicArticleDetailResponse;
import com.windlabs.cms.article.DTO.PublicArticleListResponse;
import com.windlabs.cms.article.Entity.ArticleEntity;
import com.windlabs.cms.article.Enum.ArticleStatus;
import com.windlabs.cms.article.Repository.ArticleRepository;
import com.windlabs.cms.author.Entity.AuthorEntity;
import com.windlabs.cms.category.Entity.CategoryEntity;
import com.windlabs.cms.common.exception.ResourceNotFoundException;
import com.windlabs.cms.common.response.PageResponse;
import com.windlabs.cms.common.response.PaginationUtil;
import com.windlabs.cms.domain.Entity.DomainEntity;
import com.windlabs.cms.domain.Enum.DomainStatus;
import com.windlabs.cms.domain.Repository.DomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PublicArticleService {

    private final ArticleRepository articleRepository;
    private final DomainRepository domainRepository;

    @Transactional(readOnly = true)
    public PageResponse<PublicArticleListResponse> getPublishedArticlesByHost(String host, int page, int size) {
        DomainEntity domain = findActiveDomainByHost(host);
    
        Pageable pageable = PaginationUtil.createPageable(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "publishedAt")
        );
    
        Page<ArticleEntity> articlePage = articleRepository.findByDomain_IdAndStatus(
                domain.getId(),
                ArticleStatus.PUBLISHED,
                pageable
        );
    
        List<PublicArticleListResponse> items = articlePage.getContent()
                .stream()
                .map(this::toPublicListResponse)
                .toList();
    
        return PageResponse.of(articlePage, items);
    }

    @Transactional(readOnly = true)
    public PublicArticleDetailResponse getPublishedArticleByHostAndSlug(String host, String slug) {
        DomainEntity domain = findActiveDomainByHost(host);

        String normalizedSlug = slugify(slug);

        ArticleEntity article = articleRepository
                .findByDomain_IdAndSlugIgnoreCaseAndStatus(
                        domain.getId(),
                        normalizedSlug,
                        ArticleStatus.PUBLISHED
                )
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Published article not found with slug: " + normalizedSlug
                ));

        return toPublicDetailResponse(article);
    }

    @Transactional(readOnly = true)
    public PageResponse<PublicArticleListResponse> getPublishedArticlesByHostAndCategorySlug(
            String host,
            String categorySlug,
            int page,
            int size
    ) {
        DomainEntity domain = findActiveDomainByHost(host);
    
        String normalizedCategorySlug = slugify(categorySlug);
    
        Pageable pageable = PaginationUtil.createPageable(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "publishedAt")
        );
    
        Page<ArticleEntity> articlePage = articleRepository
                .findByDomain_IdAndCategory_SlugIgnoreCaseAndStatus(
                        domain.getId(),
                        normalizedCategorySlug,
                        ArticleStatus.PUBLISHED,
                        pageable
                );
    
        List<PublicArticleListResponse> items = articlePage.getContent()
                .stream()
                .map(this::toPublicListResponse)
                .toList();
    
        return PageResponse.of(articlePage, items);
    }

    private DomainEntity findActiveDomainByHost(String host) {
        String normalizedHost = normalizeHost(host);

        DomainEntity domain = domainRepository.findByHostIgnoreCase(normalizedHost)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Domain not found with host: " + normalizedHost
                ));

        if (domain.getStatus() != DomainStatus.ACTIVE) {
            throw new ResourceNotFoundException(
                    "Domain is not active: " + normalizedHost
            );
        }

        return domain;
    }

    private PublicArticleListResponse toPublicListResponse(ArticleEntity article) {
        AuthorEntity author = article.getAuthor();
        CategoryEntity category = article.getCategory();

        return PublicArticleListResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .slug(article.getSlug())
                .excerpt(article.getExcerpt())
                .featuredImageUrl(article.getFeaturedImageUrl())
                .seoTitle(article.getSeoTitle())
                .seoDescription(article.getSeoDescription())
                .publishedAt(article.getPublishedAt())
                .authorName(author != null ? author.getName() : null)
                .authorSlug(author != null ? author.getSlug() : null)
                .categoryName(category != null ? category.getName() : null)
                .categorySlug(category != null ? category.getSlug() : null)
                .build();
    }

    private PublicArticleDetailResponse toPublicDetailResponse(ArticleEntity article) {
        DomainEntity domain = article.getDomain();
        AuthorEntity author = article.getAuthor();
        CategoryEntity category = article.getCategory();

        return PublicArticleDetailResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .slug(article.getSlug())
                .excerpt(article.getExcerpt())
                .content(article.getContent())
                .seoTitle(article.getSeoTitle())
                .seoDescription(article.getSeoDescription())
                .canonicalUrl(article.getCanonicalUrl())
                .featuredImageUrl(article.getFeaturedImageUrl())
                .publishedAt(article.getPublishedAt())

                .domainName(domain.getName())
                .domainHost(domain.getHost())

                .authorName(author != null ? author.getName() : null)
                .authorSlug(author != null ? author.getSlug() : null)
                .authorBio(author != null ? author.getBio() : null)
                .authorAvatarUrl(author != null ? author.getAvatarUrl() : null)

                .categoryName(category != null ? category.getName() : null)
                .categorySlug(category != null ? category.getSlug() : null)
                .build();
    }

    private String normalizeHost(String host) {
        if (host == null || host.isBlank()) {
            throw new ResourceNotFoundException("Host is required");
        }

        return host
                .trim()
                .toLowerCase(Locale.ROOT)
                .replace("https://", "")
                .replace("http://", "")
                .replace("www.", "")
                .replaceAll("/$", "");
    }

    private String slugify(String value) {
        if (value == null || value.isBlank()) {
            throw new ResourceNotFoundException("Slug is required");
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
            throw new ResourceNotFoundException("Invalid slug");
        }

        return slug;
    }
}