package com.leyou.search.pjo;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;

import java.util.List;
import java.util.Map;

public class SearchResult extends PageResult<Goods> {

    private  List<Map<String, Object>> categories;
    private List<Brand> brands;

    public SearchResult() {
    }

    public SearchResult(List<Map<String, Object>> categories, List<Brand> brands) {
        this.categories = categories;
        this.brands = brands;
    }

    public SearchResult(List<Goods> items, Long total, List<Map<String, Object>> categories, List<Brand> brands) {
        super(items, total);
        this.categories = categories;
        this.brands = brands;
    }

    public SearchResult(List<Goods> items, Long total, Integer totalPage, List<Map<String, Object>> categories, List<Brand> brands) {
        super(items, total, totalPage);
        this.categories = categories;
        this.brands = brands;
    }

    public List<Map<String, Object>> getCategories() {
        return categories;
    }

    public void setCategories(List<Map<String, Object>> categories) {
        this.categories = categories;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }
}
