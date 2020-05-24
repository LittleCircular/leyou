package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IndexService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecClient specClient;

    @Autowired
    private GoodsRepository goodsRepository;

    public Goods buildGoods(SpuBo spuBo) {
        //SpuBo -> Goods
        Goods goods = new Goods();
        //复制
        BeanUtils.copyProperties(spuBo,goods);

        //根据spubo里的分类id查询分类名称
        List<String> names = categoryClient.queryNamesByIds(Arrays.asList(spuBo.getCid1(), spuBo.getCid2(), spuBo.getCid3()));
        //拼接all 标题+分类
        String all = spuBo.getTitle() + " " + StringUtils.join(names," ");
        goods.setAll(all);

        //根据spu的id查询sku
        List<Sku> skus = goodsClient.querySkuBySpuId(spuBo.getId());

        //装部分字段的sku
        List<Map<String,Object>> list = new ArrayList<>();
        //装price
        List<Long> prices = new ArrayList<>();
        for (Sku sku : skus) {
            //获取price
            prices.add(sku.getPrice());
            //获取部分字段的的sku放入map
            Map<String,Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            map.put("image",StringUtils.isBlank(sku.getImages())?"":sku.getImages().split(",")[0]);
            list.add(map);
        }

        //list -> json格式字符串
        goods.setSkus(JsonUtils.serialize(list));
        goods.setPrice(prices);

        Map<String,Object> specsMap = getSpecs(spuBo);
        goods.setSpecs(specsMap);
        return goods;
    }

    //根据spu查询可搜索的规格参数
    private Map<String, Object> getSpecs(SpuBo spuBo) {
        Map<String,Object> specs = new HashMap<>();
        //查询spuDetial
        SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spuBo.getId());

        //查询可搜索的规格参数
        List<SpecParam> specParams = specClient.querySpecParam(null, spuBo.getCid3(), true, null);

        //spuDetial中通用的规格参数 -> map
        //{"1":"1其它","2":"1G9青春版（全网通版）","3":"12016","5":"1143","6":"1其它","7":"1Android","8":"1骁龙（Snapdragon)","9":"1骁龙617（msm8952）","10":"1八核","11":"11.5","14":"15.2","15":"11920*1080(FHD)","16":"1800","17":"11300","18":"13000"}
        Map<Long, Object> genericMap = JsonUtils.nativeRead(spuDetail.getGenericSpec(), new TypeReference<Map<Long, Object>>() {
        });

        //spuDetial中特有的规格参数 -> map
        //{"4":["1白色","1金色"],"12":["3GB"],"13":["16GB"]}
        Map<Long, List<String>> specialMap = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
        });

        for (SpecParam specParam : specParams) {
            Long id = specParam.getId();
            String name = specParam.getName();
            Object value = null;
            //通用规格参数
            if (specParam.getGeneric()) {
                value = genericMap.get(id);//value map.get(key)
                //如果是数值型，分段
                if (null != value && specParam.getNumeric()) {
                    chooseSegment(value.toString(),specParam);
                }
            }else {//特有规格参数
                value = specialMap.get(id);
            }
            if (null == value) {
                value = "其他";
            }
            specs.put(name,value);
        }
        return specs;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {//segment:1000-2000
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();//添加单位
                }
                break;
            }
        }
        return result;
    }

    public void createIndex(Long id) {
        //id -> spu -> spuBo -> goods
        Spu spu = goodsClient.querySpuById(id);
        SpuBo spuBo = new SpuBo();
        BeanUtils.copyProperties(spu,spuBo);

        Goods goods = buildGoods(spuBo);

        goodsRepository.save(goods);
    }

    public void deleteIndex(Long id) {
        goodsRepository.deleteById(id);
    }
}
