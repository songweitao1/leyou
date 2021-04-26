package com.leyou.search.pjo;


public class SearchRequest {
    private String key;
    private Integer page;
    private String sortBy;
    private boolean descending;
    private static final Integer DEFAULT_SIZE = 20;
    private static final Integer DEFAULT_PAGE = 1;

    public String getSortBy() {
        return sortBy;
    }
    public boolean getDescending() {
        return descending;
    }
    public String getKey() {
        return key;
    }
    public Integer getPage() {
        if (page == null) {
            return DEFAULT_PAGE;
        }
        return Math.max(DEFAULT_PAGE, page);

    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return DEFAULT_SIZE;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
