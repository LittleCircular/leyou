package com.leyou.search.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.search.pojo.Goods;
import com.leyou.search.service.SearchService;
import com.leyou.search.utils.SearchRequest;
import com.leyou.search.utils.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    //http://api.leyou.com/api/search/page
//    @PostMapping("page")
//    public ResponseEntity<PageResult<Goods>> Search(@RequestBody SearchRequest searchRequest){
//        PageResult<Goods> goodsPageResult = searchService.search(searchRequest);
//        if (null != goodsPageResult && goodsPageResult.getItems().size() > 0) {
//            return ResponseEntity.ok(goodsPageResult);
//        }
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }
    //http://api.leyou.com/api/search/page
    @PostMapping("page")
    public ResponseEntity<SearchResult> Search(@RequestBody SearchRequest searchRequest){
        SearchResult searchResult = searchService.search(searchRequest);
        if (null != searchResult && searchResult.getItems().size() > 0) {
            return ResponseEntity.ok(searchResult);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
