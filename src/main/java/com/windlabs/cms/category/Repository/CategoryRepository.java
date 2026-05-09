package com.windlabs.cms.category.Repository;

import com.windlabs.cms.category.Entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {

    boolean existsByDomain_IdAndSlugIgnoreCase(UUID domainId, String slug);

    Optional<CategoryEntity> findByDomain_IdAndSlugIgnoreCase(UUID domainId, String slug);

    List<CategoryEntity> findByDomain_IdOrderByNameAsc(UUID domainId);
    Page<CategoryEntity> findByDomain_Id(UUID domainId, Pageable pageable);
}