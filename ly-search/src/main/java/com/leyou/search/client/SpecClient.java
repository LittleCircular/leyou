package com.leyou.search.client;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "item-service")
public interface SpecClient {
    @GetMapping("spec/groups/{cid}")
    public List<SpecGroup> querySpecGroups(@PathVariable("cid") Long cid);

    //http://api.leyou.com/api/item/spec/params?gid=1
    /**
     * 根据 规格组id 查询 规格参数
     * @param gid
     * @param cid
     * @param searching
     * @param generic
     * @return
     */
    @GetMapping("spec/params")
    public List<SpecParam> querySpecParam(@RequestParam(value = "gid",required = false) Long gid,
                                                          @RequestParam(value = "cid",required = false) Long cid,
                                                          @RequestParam(value = "searching",required = false) Boolean searching,
                                                          @RequestParam(value = "generic",required = false) Boolean generic);

}
