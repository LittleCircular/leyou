package com.leyou.item.service;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> queryByParentId(Long id) {
        //select * from tb_category where parent_id = 0
        Category category = new Category();
        category.setParentId(id);
        return categoryMapper.select(category);
    }

    public List<Category> queryByBrandId(Long bid) {
        //根据品牌id查询所有的分类
        return categoryMapper.queryByBrandId(bid);
    }

    public List<String> queryNameByIds(List<Long> cids) {
        //select * from tb_category where id in (75,76,77)
        List<String> names = new ArrayList<>();
        List<Category> categories = categoryMapper.selectByIdList(cids);

        for (Category category : categories){
            names.add(category.getName());
        }
        return names;
    }
}
