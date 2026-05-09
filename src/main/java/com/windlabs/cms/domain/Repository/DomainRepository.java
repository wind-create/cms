package com.windlabs.cms.domain.Repository;

import com.windlabs.cms.domain.Entity.DomainEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DomainRepository extends JpaRepository<DomainEntity, UUID> {

    boolean existsByHostIgnoreCase(String host);

    Optional<DomainEntity> findByHostIgnoreCase(String host);
}