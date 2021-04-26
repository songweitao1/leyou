package com.leyou.search.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.search.pjo.Goods;
import com.leyou.search.pjo.SearchRequest;
import com.leyou.search.pjo.SearchResult;
import com.leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class SearchController {
    @Autowired
    SearchService searchService;
    @PostMapping("page")
    public ResponseEntity<SearchResult> search(@RequestBody SearchRequest searchRequest){
        SearchResult searchResult = this.searchService.search(searchRequest);
        if (searchRequest == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(searchResult);
    }
}
