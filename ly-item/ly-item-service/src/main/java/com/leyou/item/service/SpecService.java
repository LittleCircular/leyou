package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Transient;
import java.util.List;

@Service
public class SpecService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    public List<SpecGroup> querySpecGroups(Long cid) {
        //select * from tb_spec_group where cid = 76
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);

        //查询规格组表
        List<SpecGroup> specGroups = specGroupMapper.select(specGroup);
        //查询每个规格组的规格参数
        specGroups.forEach(record -> {
            SpecParam specParam = new SpecParam();
            specParam.setGroupId(record.getId());

            //根据规格组的id查询规格参数
            //select * from tb_spec_param where group_id = 1
            List<SpecParam> specParams = specParamMapper.select(specParam);
            //把查到该组的规格参数set到该组
            record.setSpecParams(specParams);

        });

        return specGroups;
    }

    public List<SpecParam> querySpecParam(Long gid,Long cid,Boolean searching,Boolean generic) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);//规格组id
        specParam.setCid(cid);//分类id
        specParam.setSearching(searching);//是否可以搜索
        specParam.setGeneric(generic);//是否是通用属性
        //select * from tb_spec_param where group_id = 1
        List<SpecParam> specParams = specParamMapper.select(specParam);
        return  specParams;
    }

    @Transient
    public void addSpecGroup(SpecGroup specGroup) {
        specGroupMapper.insert(specGroup);
    }

    @Transient
    public void updateSpecGroup(SpecGroup specGroup) {
        specGroupMapper.updateByPrimaryKey(specGroup);
    }

    @Transient
    public void delSpecGroup(SpecGroup specGroup) {
        specGroupMapper.deleteByPrimaryKey(specGroup);
    }

    @Transient
    public void addSpecParam(SpecParam specParam) {
        specParamMapper.insert(specParam);
    }

    @Transient
    public void updataSpecParam(SpecParam specParam) {
        specParamMapper.updateByPrimaryKey(specParam);
    }

    public void delSpecParam(SpecParam specParam) {
        specParamMapper.deleteByPrimaryKey(specParam);
    }
}
