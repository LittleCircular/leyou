package com.leyou.item.controller;

import com.github.pagehelper.PageException;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    //http://api.leyou.com/api/item/spu/page?key=&saleable=true&page=1&rows=5
    /**
     * 查询商品
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */
    @RequestMapping("spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuByPage(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                                            @RequestParam(value = "rows",defaultValue = "10") Integer rows,
                                                            @RequestParam(value = "saleable",required = false) Boolean saleable,
                                                            @RequestParam(value = "key",required = false) String key){
        PageResult<SpuBo> pageResult = goodsService.querySpuByPage(page,rows,saleable,key);
        if (pageResult != null && pageResult.getItems() != null && pageResult.getItems().size() != 0){
            return ResponseEntity.ok(pageResult);//返回响应码200，数据
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //http://api.leyou.com/api/item/goods
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo){
        goodsService.saveGoods(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();//返回响应码201
    }

    //http://api.leyou.com/api/item/spu/detail/3
    @GetMapping("spu/detail/{spuid}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("spuid") Long spuid){
        SpuDetail spuDetail = goodsService.querySpuDetailBySpuId(spuid);
        if (null != spuDetail){
            return ResponseEntity.ok(spuDetail);//返回响应码200，数据
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //http://api.leyou.com/api/item/sku/list?id=2
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long spuId){
        List<Sku> skus = goodsService.querySkuBySpuId(spuId);
        if (null != skus && skus.size() > 0){
            return ResponseEntity.ok(skus);//返回响应码200，数据
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //http://api.leyou.com/api/item/goods
    /**
     * 更新商品
     * @param spuBo
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spuBo){
        System.out.println("spuBo.getSkus()=="+spuBo.getSkus());
        goodsService.updateGoods(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();//返回响应码201
    }

    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long spuId){
        Spu spu = goodsService.querySpuById(spuId);
        if (null == spu) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(spu);
    }
}
