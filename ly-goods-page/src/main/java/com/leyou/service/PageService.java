package com.leyou.service;

import com.leyou.client.GoodsClient;
import com.leyou.client.SpecClient;
import com.leyou.item.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageService {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecClient specClient;

    public Map<String, Object> loadData(Long spuId) {
        Map<String, Object> map = new HashMap<>();

        //根据spuid查询spu
        Spu spu1 = goodsClient.querySpuById(spuId);
//        let spu = /*[[${spu}]]*/ {};
//        let spuDetail = /*[[${spuDetail}]]*/ {};
//        let skus = /*[[${skus}]]*/ {};
//        let specParams = /*[[${specParams}]]*/ {};
//        let specGroups = /*[[${specGroups}]]*/ {};
        //放入map
        map.put("spu",spu1);

        //根据spuid查询spuDetail
        SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spuId);
        map.put("spuDetail",spuDetail);

        //根据spuid查询sku
        List<Sku> skuList = goodsClient.querySkuBySpuId(spuId);
        map.put("skus",skuList);

        //根据spu的三级分类查询特有规格参数
        List<SpecParam> specParams = specClient.querySpecParam(null, spu1.getCid3(), null, false);
        Map<Long, Object> spMap = new HashMap<>();
        for (SpecParam specParam : specParams) {//把规格参数id和名称放入map
            spMap.put(specParam.getId(),specParam.getName());
        }
        map.put("specParams",spMap);

        //根据spu的三级分类id查询规格组
        List<SpecGroup> specGroups = specClient.querySpecGroups(spu1.getCid3());
        map.put("specGroups",specGroups);
        return map;
    }
}
