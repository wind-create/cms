package com.windlabs.cms.common.response;

import com.windlabs.cms.common.exception.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtil {

    private PaginationUtil() {
    }

    public static Pageable createPageable(int page, int size, Sort sort) {
        if (page < 1) {
            throw new BadRequestException("Page must start from 1");
        }

        if (size < 1) {
            throw new BadRequestException("Size must be greater than 0");
        }

        if (size > 100) {
            throw new BadRequestException("Maximum size is 100");
        }

        return PageRequest.of(page - 1, size, sort);
    }
}