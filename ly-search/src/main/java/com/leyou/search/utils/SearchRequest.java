package com.leyou.search.utils;

import java.util.Map;

//接收浏览器传过来的数据
public class SearchRequest {

    private String key;//搜索关键字
    private Integer page;//当前页

    private Map<String,String> filter;
    private static final Integer  DEFAULT_PAGE = 1;//默认第一页
    private static final Integer  DEFAULT_SIZE = 20;//默认每页20条数据

    public Map<String, String> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, String> filter) {
        this.filter = filter;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        if (page == null) {
            return DEFAULT_PAGE;
        }
        return Math.max(page,DEFAULT_PAGE);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize(){
        return DEFAULT_SIZE;
    }
}
