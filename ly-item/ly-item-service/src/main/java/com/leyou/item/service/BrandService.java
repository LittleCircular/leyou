package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> pageQuery(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        //select count(*) from tb_brand where name like "%" + key + "%"
        //select * from tb_brand where name like "%" + key + "%" order by id asc limit 0,5
        //开启分页助手
        PageHelper.startPage(page,rows);
        //过滤
        Example example = new Example(Brand.class);//where 后面的东西
        if (StringUtils.isNoneBlank(key)){
            Example.Criteria criteria = example.createCriteria();
            criteria.andLike("name","%" + key + "%");//要做like查询的字段
        }
        if (StringUtils.isNoneBlank(sortBy)){
            example.setOrderByClause(sortBy+(desc?" DESC":" ASC"));//排序
        }
        //查询结果并转化分页条件结果
        Page<Brand> brandPage = (Page<Brand>) brandMapper.selectByExample(example);
        //通用mapper
        //select * from tb_brand where name like "%" + key + "%" order by id asc
        //分页助手 拦截sql进行拼接
        //select * from tb_brand where name like "%" + key + "%" order by id asc limit 0,5
        //select count(*) from tb_brand where name like "%" + key + "%"
        return new PageResult(brandPage.getTotal(),new Long(brandPage.getPages()),brandPage.getResult());
    }

    @Transactional//开启事务
    public void addBrand(Brand brand, List<Long> cids) {
        //新增品牌信息
        brandMapper.insertSelective(brand);//插入完成返回主键到brand

//        for (Long cid : cids){
//            //品牌分类关系表
//            brandMapper.insertBrandCategory(brand.getId(),cid);
//        }
        cids.forEach(cid -> {
            brandMapper.insertBrandCategory(brand.getId(),cid);
        });
    }

    @Transactional
    public void updateBrand(Brand brand, List<Long> cids) {
        //更新品牌
        brandMapper.updateByPrimaryKey(brand);
        //先删除品牌之前的分类，然后再重新关联品牌和分类
        //删除
        brandMapper.deleteBrandCategory(brand.getId());
        //插入
        cids.forEach(cid -> {
            brandMapper.insertBrandCategory(brand.getId(),cid);
        });
    }

    public List<Brand> queryBrandByCategory(Long cid) {
        return brandMapper.queryBrandByCategory(cid);
    }

    public Brand queryBrandById(Long bid) {
        System.out.println("**brandService1111**"+bid);
        Brand brand = this.brandMapper.selectByPrimaryKey(bid);
        System.out.println("**brandService2222**"+brand);
        return brand;
    }
}
