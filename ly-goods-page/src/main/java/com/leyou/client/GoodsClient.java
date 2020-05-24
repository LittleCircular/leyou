package com.leyou.client;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "item-service")
public interface GoodsClient {

    @RequestMapping("spu/page")
    public PageResult<SpuBo> querySpuByPage(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                            @RequestParam(value = "rows",defaultValue = "10") Integer rows,
                                            @RequestParam(value = "saleable",required = false) Boolean saleable,
                                            @RequestParam(value = "key",required = false) String key);

    //http://api.leyou.com/api/item/spu/detail/3
    @GetMapping("spu/detail/{spuid}")
    public SpuDetail querySpuDetailBySpuId(@PathVariable("spuid") Long spuid);

    //http://api.leyou.com/api/item/sku/list?id=2
    @GetMapping("sku/list")
    public List<Sku> querySkuBySpuId(@RequestParam("id") Long spuId);

    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    public Spu querySpuById(@PathVariable("id") Long spuId);

}
