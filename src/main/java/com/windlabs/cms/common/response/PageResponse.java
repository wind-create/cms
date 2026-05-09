package com.windlabs.cms.common.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class PageResponse<T> {

    private List<T> items;

    private Integer page;

    private Integer size;

    private Long totalElements;

    private Integer totalPages;

    private Boolean hasNext;

    private Boolean hasPrevious;

    private Boolean first;

    private Boolean last;

    public static <T> PageResponse<T> of(Page<?> pageData, List<T> items) {
        return PageResponse.<T>builder()
                .items(items)
                .page(pageData.getNumber() + 1)
                .size(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .hasNext(pageData.hasNext())
                .hasPrevious(pageData.hasPrevious())
                .first(pageData.isFirst())
                .last(pageData.isLast())
                .build();
    }
}