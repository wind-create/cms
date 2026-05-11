package com.windlabs.cms.article.Service;

import com.windlabs.cms.article.DTO.ArticleResponse;
import com.windlabs.cms.article.DTO.CreateArticleRequest;
import com.windlabs.cms.article.DTO.UpdateArticleRequest;
import com.windlabs.cms.article.Entity.ArticleEntity;
import com.windlabs.cms.article.Enum.ArticleStatus;
import com.windlabs.cms.article.Repository.ArticleRepository;
import com.windlabs.cms.author.Entity.AuthorEntity;
import com.windlabs.cms.author.Repository.AuthorRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final DomainRepository domainRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ArticleResponse createArticle(CreateArticleRequest request, UUID currentUserId) {
        DomainEntity domain = findDomainOrThrow(request.getDomainId());

        AuthorEntity author = null;
        if (request.getAuthorId() != null) {
            author = findAuthorOrThrow(request.getAuthorId());
        }

        CategoryEntity category = null;
        if (request.getCategoryId() != null) {
            category = findCategoryOrThrow(request.getCategoryId());
            validateCategoryBelongsToDomain(category, domain.getId());
        }

        String slug = resolveSlug(request.getSlug(), request.getTitle());

        if (articleRepository.existsByDomain_IdAndSlugIgnoreCase(domain.getId(), slug)) {
            throw new BadRequestException("Article slug already exists in this domain: " + slug);
        }

        ArticleStatus status = request.getStatus() != null ? request.getStatus() : ArticleStatus.DRAFT;

        validateStatusRules(status, request.getScheduledAt());

        LocalDateTime publishedAt = null;

        if (status == ArticleStatus.PUBLISHED) {
            publishedAt = LocalDateTime.now();
        }

        ArticleEntity article = ArticleEntity.builder()
                .domain(domain)
                .author(author)
                .category(category)
                .title(request.getTitle().trim())
                .slug(slug)
                .excerpt(normalizeNullableText(request.getExcerpt()))
                .content(request.getContent().trim())
                .status(status)
                .seoTitle(normalizeNullableText(request.getSeoTitle()))
                .seoDescription(normalizeNullableText(request.getSeoDescription()))
                .canonicalUrl(normalizeNullableText(request.getCanonicalUrl()))
                .featuredImageUrl(normalizeNullableText(request.getFeaturedImageUrl()))
                .scheduledAt(request.getScheduledAt())
                .publishedAt(publishedAt)
                .createdBy(currentUserId)
                .updatedBy(currentUserId)
                .build();

        ArticleEntity savedArticle = articleRepository.save(article);

        return toResponse(savedArticle);
    }

    @Transactional(readOnly = true)
    public PageResponse<ArticleResponse> getAllArticles(int page, int size) {
        Pageable pageable = PaginationUtil.createPageable(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    
        Page<ArticleEntity> articlePage = articleRepository.findAll(pageable);
    
        List<ArticleResponse> items = articlePage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();
    
        return PageResponse.of(articlePage, items);
    }

    @Transactional(readOnly = true)
    public ArticleResponse getArticleById(UUID id) {
        ArticleEntity article = findArticleOrThrow(id);

        return toResponse(article);
    }

    @Transactional(readOnly = true)
    public PageResponse<ArticleResponse> getArticlesByDomainId(UUID domainId, int page, int size) {
        findDomainOrThrow(domainId);
    
        Pageable pageable = PaginationUtil.createPageable(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    
        Page<ArticleEntity> articlePage = articleRepository.findByDomain_Id(domainId, pageable);
    
        List<ArticleResponse> items = articlePage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();
    
        return PageResponse.of(articlePage, items);
    }

    @Transactional(readOnly = true)
    public ArticleResponse getArticleByDomainAndSlug(UUID domainId, String slug) {
        findDomainOrThrow(domainId);

        String normalizedSlug = slugify(slug);

        ArticleEntity article = articleRepository
                .findByDomain_IdAndSlugIgnoreCase(domainId, normalizedSlug)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Article not found with slug: " + normalizedSlug + " in domain id: " + domainId
                ));

        return toResponse(article);
    }

    @Transactional
    public ArticleResponse updateArticle(UUID id, UpdateArticleRequest request, UUID currentUserId) {
        ArticleEntity article = findArticleOrThrow(id);

        DomainEntity targetDomain = article.getDomain();

        if (request.getDomainId() != null) {
            targetDomain = findDomainOrThrow(request.getDomainId());
            article.setDomain(targetDomain);

            if (article.getCategory() != null) {
                validateCategoryBelongsToDomain(article.getCategory(), targetDomain.getId());
            }
        }

        if (request.getAuthorId() != null) {
            AuthorEntity author = findAuthorOrThrow(request.getAuthorId());
            article.setAuthor(author);
        }

        if (request.getCategoryId() != null) {
            CategoryEntity category = findCategoryOrThrow(request.getCategoryId());
            validateCategoryBelongsToDomain(category, targetDomain.getId());
            article.setCategory(category);
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            article.setTitle(request.getTitle().trim());
        }

        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            String normalizedSlug = slugify(request.getSlug());

            validateUniqueSlugForUpdate(
                    article.getId(),
                    targetDomain.getId(),
                    normalizedSlug
            );

            article.setSlug(normalizedSlug);
        } else if (request.getDomainId() != null) {
            validateUniqueSlugForUpdate(
                    article.getId(),
                    targetDomain.getId(),
                    article.getSlug()
            );
        }

        if (request.getExcerpt() != null) {
            article.setExcerpt(normalizeNullableText(request.getExcerpt()));
        }

        if (request.getContent() != null && !request.getContent().isBlank()) {
            article.setContent(request.getContent().trim());
        }

        if (request.getStatus() != null) {
            validateStatusRules(request.getStatus(), request.getScheduledAt());

            ArticleStatus oldStatus = article.getStatus();
            ArticleStatus newStatus = request.getStatus();

            article.setStatus(newStatus);

            if (newStatus == ArticleStatus.PUBLISHED && oldStatus != ArticleStatus.PUBLISHED) {
                article.setPublishedAt(LocalDateTime.now());
            }

            if (newStatus != ArticleStatus.SCHEDULED) {
                article.setScheduledAt(null);
            }
        }

        if (request.getSeoTitle() != null) {
            article.setSeoTitle(normalizeNullableText(request.getSeoTitle()));
        }

        if (request.getSeoDescription() != null) {
            article.setSeoDescription(normalizeNullableText(request.getSeoDescription()));
        }

        if (request.getCanonicalUrl() != null) {
            article.setCanonicalUrl(normalizeNullableText(request.getCanonicalUrl()));
        }

        if (request.getFeaturedImageUrl() != null) {
            article.setFeaturedImageUrl(normalizeNullableText(request.getFeaturedImageUrl()));
        }

        if (request.getScheduledAt() != null) {
            article.setScheduledAt(request.getScheduledAt());
        }

        if (request.getUpdatedBy() != null) {
            article.setUpdatedBy(currentUserId);
        }

        ArticleEntity updatedArticle = articleRepository.save(article);

        return toResponse(updatedArticle);
    }

    @Transactional
    public ArticleResponse publishArticle(UUID id) {
        ArticleEntity article = findArticleOrThrow(id);

        article.setStatus(ArticleStatus.PUBLISHED);
        article.setPublishedAt(LocalDateTime.now());
        article.setScheduledAt(null);

        return toResponse(articleRepository.save(article));
    }

    @Transactional
    public ArticleResponse unpublishArticle(UUID id) {
        ArticleEntity article = findArticleOrThrow(id);

        article.setStatus(ArticleStatus.DRAFT);
        article.setPublishedAt(null);
        article.setScheduledAt(null);

        return toResponse(articleRepository.save(article));
    }

    @Transactional
    public ArticleResponse archiveArticle(UUID id) {
        ArticleEntity article = findArticleOrThrow(id);

        article.setStatus(ArticleStatus.ARCHIVED);

        return toResponse(articleRepository.save(article));
    }

    @Transactional
    public void deleteArticle(UUID id) {
        ArticleEntity article = findArticleOrThrow(id);

        articleRepository.delete(article);
    }

    private ArticleEntity findArticleOrThrow(UUID id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + id));
    }

    private DomainEntity findDomainOrThrow(UUID domainId) {
        return domainRepository.findById(domainId)
                .orElseThrow(() -> new ResourceNotFoundException("Domain not found with id: " + domainId));
    }

    private AuthorEntity findAuthorOrThrow(UUID authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));
    }

    private CategoryEntity findCategoryOrThrow(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }

    private void validateCategoryBelongsToDomain(CategoryEntity category, UUID domainId) {
        if (!category.getDomain().getId().equals(domainId)) {
            throw new BadRequestException("Category does not belong to selected domain");
        }
    }

    private void validateUniqueSlugForUpdate(UUID currentArticleId, UUID domainId, String slug) {
        articleRepository.findByDomain_IdAndSlugIgnoreCase(domainId, slug)
                .ifPresent(existingArticle -> {
                    if (!existingArticle.getId().equals(currentArticleId)) {
                        throw new BadRequestException("Article slug already exists in this domain: " + slug);
                    }
                });
    }

    private void validateStatusRules(ArticleStatus status, LocalDateTime scheduledAt) {
        if (status == ArticleStatus.SCHEDULED && scheduledAt == null) {
            throw new BadRequestException("Scheduled article must have scheduledAt value");
        }
    }

    private ArticleResponse toResponse(ArticleEntity article) {
        DomainEntity domain = article.getDomain();
        AuthorEntity author = article.getAuthor();
        CategoryEntity category = article.getCategory();

        return ArticleResponse.builder()
                .id(article.getId())

                .domainId(domain.getId())
                .domainName(domain.getName())
                .domainHost(domain.getHost())

                .authorId(author != null ? author.getId() : null)
                .authorName(author != null ? author.getName() : null)
                .authorSlug(author != null ? author.getSlug() : null)

                .categoryId(category != null ? category.getId() : null)
                .categoryName(category != null ? category.getName() : null)
                .categorySlug(category != null ? category.getSlug() : null)

                .title(article.getTitle())
                .slug(article.getSlug())
                .excerpt(article.getExcerpt())
                .content(article.getContent())
                .status(article.getStatus())

                .seoTitle(article.getSeoTitle())
                .seoDescription(article.getSeoDescription())
                .canonicalUrl(article.getCanonicalUrl())
                .featuredImageUrl(article.getFeaturedImageUrl())

                .publishedAt(article.getPublishedAt())
                .scheduledAt(article.getScheduledAt())

                .createdBy(article.getCreatedBy())
                .updatedBy(article.getUpdatedBy())

                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .build();
    }

    private String resolveSlug(String requestSlug, String title) {
        if (requestSlug != null && !requestSlug.isBlank()) {
            return slugify(requestSlug);
        }

        return slugify(title);
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

    @Transactional(readOnly = true)
    public PageResponse<ArticleResponse> getArticlesByAuthorId(UUID authorId, int page, int size) {
        findAuthorOrThrow(authorId);
    
        Pageable pageable = PaginationUtil.createPageable(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    
        Page<ArticleEntity> articlePage = articleRepository.findByAuthor_Id(authorId, pageable);
    
        List<ArticleResponse> items = articlePage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();
    
        return PageResponse.of(articlePage, items);
    }
}