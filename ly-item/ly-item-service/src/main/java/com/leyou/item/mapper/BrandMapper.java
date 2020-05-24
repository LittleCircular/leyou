package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {

    @Insert("insert into tb_category_brand(brand_id,category_id) values(#{id},#{cid})")
    void insertBrandCategory(Long id, Long cid);

    @Delete("delete from tb_category_brand where brand_id=#{id}")
    void deleteBrandCategory(Long id);

    @Select("select tb.* from tb_brand tb left join tb_category_brand tc on tb.id=tc.brand_id where tc.category_id=#{cid}")
    List<Brand> queryBrandByCategory(Long cid);
}
