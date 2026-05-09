package com.windlabs.cms.author.Repository;

import com.windlabs.cms.author.Entity.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthorRepository extends JpaRepository<AuthorEntity, UUID> {

    boolean existsBySlugIgnoreCase(String slug);

    Optional<AuthorEntity> findBySlugIgnoreCase(String slug);
}