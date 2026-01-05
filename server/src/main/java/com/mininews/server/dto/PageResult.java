package com.mininews.server.dto;

import java.util.List;

/**
 * 统一分页返回，便于前端处理：
 * page: 当前页（从 1 开始）
 * size: 每页大小
 * total: 总条数
 * totalPages: 总页数
 * items: 数据列表
 */
public class PageResult<T> {

    private int page;
    private int size;
    private long total;
    private int totalPages;
    private List<T> items;

    public PageResult() {
    }

    public PageResult(int page, int size, long total, int totalPages, List<T> items) {
        this.page = page;
        this.size = size;
        this.total = total;
        this.totalPages = totalPages;
        this.items = items;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotal() {
        return total;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<T> getItems() {
        return items;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
