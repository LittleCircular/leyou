package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    public PageResult<SpuBo> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {
        //开启分页助手
        PageHelper.startPage(page,rows);
        //创建查询条件
        Example example = new Example(Spu.class);
        //查询条件的构建工具
        Example.Criteria criteria = example.createCriteria();
        //是否模糊查询
        if (StringUtils.isNoneBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        //是否上架
        if (saleable != null){
            criteria.andEqualTo("saleable",saleable);
        }

        //查询spu表 select * from tb_spu where title like "%"+key+"%" and saleable=false
        List<Spu> spus = spuMapper.selectByExample(example);
        //转成分页助手的page对象
        Page<Spu> spuPage = (Page<Spu>) spus;
        //用来存放SpuBo
        List<SpuBo> spuBos = new ArrayList<>();

        for (Spu spu : spus){
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu,spuBo);//把spu中的属性值拷贝到spuBo里
            //根据spu里商品分类的id查询出商品分类的名称
            List<String> names =  categoryService.queryNameByIds(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));

            spuBo.setCname(StringUtils.join(names, "/"));//设置分类的名称

            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());//设置品牌名称

            spuBos.add(spuBo);
        }

        return new PageResult<>(spuPage.getTotal(),new Long(spuPage.getPages()),spuBos);
    }

    @Transactional
    public void saveGoods(SpuBo spuBo) {
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(new Date());
        //保存spu
        spuMapper.insertSelective(spuBo);

        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        //保存SpuDetail
        spuDetailMapper.insertSelective(spuDetail);

        //保存sku和stock
        List<Sku> skus = spuBo.getSkus();
        saveSkus(spuBo, skus);

        send("insert",spuBo.getId());

    }

    public void saveSkus(SpuBo spuBo, List<Sku> skus) {
        for (Sku sku : skus) {
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(new Date());
            //保存sku
            skuMapper.insertSelective(sku);

            Stock stock = new Stock();
            System.out.println("sku.getId()=="+sku.getId());
            stock.setSkuId(sku.getId());
            System.out.println("sku.getStock()=="+sku.getStock());
            stock.setStock(sku.getStock());
            System.out.println("stock=="+stock);
            stockMapper.insert(stock);

        }
    }

    public SpuDetail querySpuDetailBySpuId(Long spuid) {
        return spuDetailMapper.selectByPrimaryKey(spuid);
    }


    public List<Sku> querySkuBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        //select * from tb_sku where spu_id = 2
        List<Sku> skuList = skuMapper.select(sku);
        for (Sku sku1 : skuList) {
            Long id = sku1.getId();//sku的id
            //根据sku的id查询库存表
            Stock stock = stockMapper.selectByPrimaryKey(id);
            sku.setStock(stock.getStock());
        }
        return skuList;
    }

    @Transactional
    public void updateGoods(SpuBo spuBo) {
        spuBo.setLastUpdateTime(new Date());
        //更新spu
        spuMapper.updateByPrimaryKeySelective(spuBo);

        //更新spuDetail
        spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        //删除sku和stock的老数据，然后插入

        //查询sku的数据
        Sku sku = new Sku();
        sku.setSpuId(spuBo.getId());
        List<Sku> skuList = skuMapper.select(sku);
//        for (Sku sku1 : select) {
//            Long id = sku1.getId();//获取sku的id
//            //根据sku的id删除stock
//            //delete from tb_stock where sku_id = 2
//            stockMapper.deleteByPrimaryKey(id);
//            //删除sku
//            skuMapper.delete(sku1);
//        }
        if (!CollectionUtils.isEmpty(skuList)){
            //delete from tb_stock where sku_id in()
            List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
            stockMapper.deleteByIdList(ids);
            skuMapper.delete(sku);
        }
        //插入
        saveSkus(spuBo,spuBo.getSkus());

        //重建索引（elasticsearch）
        //更换静态页面（不能在商品服务里面做，不然商品模块与搜索模块耦合太高）

        //发送一条消息（到搜索模块，让搜索模块干活）
        send("update",spuBo.getId());
    }

    //发送消息
    public void send(String type, Long id){
        amqpTemplate.convertAndSend("item."+type,id);
    }

    public Spu querySpuById(Long spuId) {
        return spuMapper.selectByPrimaryKey(spuId);
    }
}
