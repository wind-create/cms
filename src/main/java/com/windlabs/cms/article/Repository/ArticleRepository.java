package com.windlabs.cms.article.Repository;

import com.windlabs.cms.article.Entity.ArticleEntity;
import com.windlabs.cms.article.Enum.ArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArticleRepository extends JpaRepository<ArticleEntity, UUID> {

    boolean existsByDomain_IdAndSlugIgnoreCase(UUID domainId, String slug);

    Optional<ArticleEntity> findByDomain_IdAndSlugIgnoreCase(UUID domainId, String slug);

    List<ArticleEntity> findByDomain_IdOrderByCreatedAtDesc(UUID domainId);

    List<ArticleEntity> findByStatusOrderByCreatedAtDesc(ArticleStatus status);


    List<ArticleEntity> findByAuthor_IdOrderByCreatedAtDesc(UUID authorId);

    List<ArticleEntity> findByDomain_IdAndStatusOrderByPublishedAtDesc(
            UUID domainId,
            ArticleStatus status
    );
    
    Optional<ArticleEntity> findByDomain_IdAndSlugIgnoreCaseAndStatus(
            UUID domainId,
            String slug,
            ArticleStatus status
    );
    
    List<ArticleEntity> findByDomain_IdAndCategory_SlugIgnoreCaseAndStatusOrderByPublishedAtDesc(
            UUID domainId,
            String categorySlug,
            ArticleStatus status
    );

    Page<ArticleEntity> findByDomain_Id(UUID domainId, Pageable pageable);

    Page<ArticleEntity> findByAuthor_Id(UUID authorId, Pageable pageable);
    
    Page<ArticleEntity> findByDomain_IdAndStatus(
            UUID domainId,
            ArticleStatus status,
            Pageable pageable
    );
    
    Page<ArticleEntity> findByDomain_IdAndCategory_SlugIgnoreCaseAndStatus(
            UUID domainId,
            String categorySlug,
            ArticleStatus status,
            Pageable pageable
    );
}