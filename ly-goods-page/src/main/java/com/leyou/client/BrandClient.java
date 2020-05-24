package com.leyou.client;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "item-service")
public interface BrandClient {
    @GetMapping("brand/page")
    public PageResult<Brand> pageQuery(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                                       @RequestParam(value = "rows",defaultValue = "10") Integer rows,
                                                       @RequestParam(value = "sortBy",required = false) String sortBy,
                                                       @RequestParam(value = "desc",required = false) Boolean desc,
                                                       @RequestParam(value = "key",required = false) String key);


    //http://api.leyou.com/api/item/brand/cid/76

    /**
     * 根据分类的id查询分类对应品牌的信息
     * @param cid
     * @return
     */
    @GetMapping("brand/cid/{cid}")
    public List<Brand> queryBrandByCategory(@PathVariable("cid") Long cid);

    /**
     * 根据品牌的id查询对应品牌的信息
     * @param bid
     * @return
     */
    @GetMapping("brand/bid/{bid}")
    public Brand queryBrandById(@PathVariable("bid") Long bid);
}
