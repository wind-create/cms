package com.windlabs.cms.domain.Entity;

import com.windlabs.cms.domain.Enum.DomainStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "domains")
public class DomainEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "host", nullable = false, unique = true, length = 255)
    private String host;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", nullable = false, columnDefinition = "domain_status")
    @Builder.Default
    private DomainStatus status = DomainStatus.ACTIVE;

    @Column(name = "default_locale", nullable = false, length = 20)
    @Builder.Default
    private String defaultLocale = "id-ID";

    @Column(name = "theme_key", nullable = false, length = 100)
    @Builder.Default
    private String themeKey = "default";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}