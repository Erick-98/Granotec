package com.granotec.inventory_api.common.dto;

import java.util.List;

public class PagedResponse<T> {
    public List<T> content;
    public int page;
    public int size;
    public long totalElements;
    public int totalPages;
}

