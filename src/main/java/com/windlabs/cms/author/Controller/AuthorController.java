package com.windlabs.cms.author.Controller;

import com.windlabs.cms.author.DTO.AuthorResponse;
import com.windlabs.cms.author.DTO.CreateAuthorRequest;
import com.windlabs.cms.author.DTO.UpdateAuthorRequest;
import com.windlabs.cms.author.Service.AuthorService;
import com.windlabs.cms.common.response.ApiResponse;
import com.windlabs.cms.common.response.PageResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${app.api.base-path}/author")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    public ResponseEntity<ApiResponse<AuthorResponse>> createAuthor(
            @Valid @RequestBody CreateAuthorRequest request
    ) {
        AuthorResponse content = authorService.createAuthor(request);

        ApiResponse<AuthorResponse> response = ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Author created successfully",
                content
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AuthorResponse>>> getAllAuthors(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<AuthorResponse> content = authorService.getAllAuthors(page, size);
    
        ApiResponse<PageResponse<AuthorResponse>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Authors retrieved successfully",
                content
        );
    
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorResponse>> getAuthorById(
            @PathVariable UUID id
    ) {
        AuthorResponse content = authorService.getAuthorById(id);

        ApiResponse<AuthorResponse> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Author retrieved successfully",
                content
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<AuthorResponse>> getAuthorBySlug(
            @PathVariable String slug
    ) {
        AuthorResponse content = authorService.getAuthorBySlug(slug);

        ApiResponse<AuthorResponse> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Author retrieved successfully",
                content
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorResponse>> updateAuthor(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAuthorRequest request
    ) {
        AuthorResponse content = authorService.updateAuthor(id, request);

        ApiResponse<AuthorResponse> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Author updated successfully",
                content
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteAuthor(
            @PathVariable UUID id
    ) {
        authorService.deleteAuthor(id);

        ApiResponse<Object> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Author deleted successfully",
                null
        );

        return ResponseEntity.ok(response);
    }
}