package com.leyou.client;

import com.leyou.item.pojo.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "item-service")
public interface CategoryClient {
    @RequestMapping("category/list")
    public List<Category> queryByParentId(@RequestParam("pid") Long id);

    //http://api.leyou.com/api/item/category/bid/325402
    @GetMapping("category/bid/{bid}")
    public List<Category> queryByBrandId(@PathVariable("bid") Long bid);

    //http://localhost:9081/category/names?ids=1,2,3
    /**
     * 根据分类id 查询分类名称
     * @param ids
     * @return
     */
    @GetMapping("category/names")
    public List<String> queryNamesByIds(@RequestParam("ids") List<Long> ids);
}
