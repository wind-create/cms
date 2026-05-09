package com.windlabs.cms.author.Service;

import com.windlabs.cms.author.DTO.AuthorResponse;
import com.windlabs.cms.author.DTO.CreateAuthorRequest;
import com.windlabs.cms.author.DTO.UpdateAuthorRequest;
import com.windlabs.cms.author.Entity.AuthorEntity;
import com.windlabs.cms.author.Repository.AuthorRepository;
import com.windlabs.cms.common.exception.BadRequestException;
import com.windlabs.cms.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.windlabs.cms.common.response.PageResponse;
import com.windlabs.cms.common.response.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Transactional
    public AuthorResponse createAuthor(CreateAuthorRequest request) {
        String slug = resolveSlug(request.getSlug(), request.getName());

        if (authorRepository.existsBySlugIgnoreCase(slug)) {
            throw new BadRequestException("Author slug already exists: " + slug);
        }

        AuthorEntity author = AuthorEntity.builder()
                .name(request.getName().trim())
                .slug(slug)
                .bio(normalizeNullableText(request.getBio()))
                .avatarUrl(normalizeNullableText(request.getAvatarUrl()))
                .build();

        AuthorEntity savedAuthor = authorRepository.save(author);

        return toResponse(savedAuthor);
    }

    @Transactional(readOnly = true)
    public PageResponse<AuthorResponse> getAllAuthors(int page, int size) {
        Pageable pageable = PaginationUtil.createPageable(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    
        Page<AuthorEntity> authorPage = authorRepository.findAll(pageable);
    
        List<AuthorResponse> items = authorPage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();
    
        return PageResponse.of(authorPage, items);
    }

    @Transactional(readOnly = true)
    public AuthorResponse getAuthorById(UUID id) {
        AuthorEntity author = findAuthorOrThrow(id);

        return toResponse(author);
    }

    @Transactional(readOnly = true)
    public AuthorResponse getAuthorBySlug(String slug) {
        String normalizedSlug = slugify(slug);

        AuthorEntity author = authorRepository.findBySlugIgnoreCase(normalizedSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with slug: " + normalizedSlug));

        return toResponse(author);
    }

    @Transactional
    public AuthorResponse updateAuthor(UUID id, UpdateAuthorRequest request) {
        AuthorEntity author = findAuthorOrThrow(id);

        if (request.getName() != null && !request.getName().isBlank()) {
            author.setName(request.getName().trim());
        }

        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            String normalizedSlug = slugify(request.getSlug());

            authorRepository.findBySlugIgnoreCase(normalizedSlug)
                    .ifPresent(existingAuthor -> {
                        if (!existingAuthor.getId().equals(id)) {
                            throw new BadRequestException("Author slug already exists: " + normalizedSlug);
                        }
                    });

            author.setSlug(normalizedSlug);
        }

        if (request.getBio() != null) {
            author.setBio(normalizeNullableText(request.getBio()));
        }

        if (request.getAvatarUrl() != null) {
            author.setAvatarUrl(normalizeNullableText(request.getAvatarUrl()));
        }

        AuthorEntity updatedAuthor = authorRepository.save(author);

        return toResponse(updatedAuthor);
    }

    @Transactional
    public void deleteAuthor(UUID id) {
        AuthorEntity author = findAuthorOrThrow(id);

        authorRepository.delete(author);
    }

    private AuthorEntity findAuthorOrThrow(UUID id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
    }

    private AuthorResponse toResponse(AuthorEntity author) {
        return AuthorResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .slug(author.getSlug())
                .bio(author.getBio())
                .avatarUrl(author.getAvatarUrl())
                .createdAt(author.getCreatedAt())
                .updatedAt(author.getUpdatedAt())
                .build();
    }

    private String resolveSlug(String requestSlug, String name) {
        if (requestSlug != null && !requestSlug.isBlank()) {
            return slugify(requestSlug);
        }

        return slugify(name);
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
}