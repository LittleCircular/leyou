package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecController {

    @Autowired
    private SpecService specService;

    //http://api.leyou.com/api/item/spec/groups/76
    /**
     * 根据 分类id 查询 规格组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroups(@PathVariable("cid") Long cid){
        List<SpecGroup> specGroups = specService.querySpecGroups(cid);
        if (specGroups != null && specGroups.size() > 0){
            return ResponseEntity.ok(specGroups);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //http://api.leyou.com/api/item/spec/params?gid=1
    /**
     * 根据 规格组id 查询 规格参数
     * @param gid
     * @param cid
     * @param searching
     * @param generic
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> querySpecParam(@RequestParam(value = "gid",required = false) Long gid,
                                                          @RequestParam(value = "cid",required = false) Long cid,
                                                          @RequestParam(value = "searching",required = false) Boolean searching,
                                                          @RequestParam(value = "generic",required = false) Boolean generic){
        List<SpecParam> specParams = specService.querySpecParam(gid,cid,searching,generic);
        if (specParams != null && specParams.size() > 0){
            return ResponseEntity.ok(specParams);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //http://api.leyou.com/api/item/spec/group
    /**
     * 添加规格组
     * @param specGroup
     * @return
     */
    @PostMapping("group")
    public ResponseEntity<Void> addSpecGroup(@RequestBody SpecGroup specGroup){
        //System.out.println("接收到的要新增的规格组"+specGroup);
        specService.addSpecGroup(specGroup);
        return ResponseEntity.status(HttpStatus.CREATED).build();//返回响应码201
    }

    //http://api.leyou.com/api/item/spec/group
    /**
     * 修改规格组
     * @param specGroup
     * @return
     */
    @PutMapping("group")
    public ResponseEntity<Void> updateSpecGroup(@RequestBody SpecGroup specGroup){
        specService.updateSpecGroup(specGroup);
        return ResponseEntity.status(HttpStatus.CREATED).build();//返回响应码201
    }

    //http://api.leyou.com/api/item/spec/group/1
    /**
     * 根据 规格组id 删除 规格组
     * @param specGroup
     * @return
     */
    @DeleteMapping("group/{gid}")
    public ResponseEntity<Void> delSpecGroup(@PathVariable("gid") Long gid){
        SpecGroup specGroup = new SpecGroup();
        specGroup.setId(gid);
        specService.delSpecGroup(specGroup);
        return ResponseEntity.status(HttpStatus.CREATED).build();//返回响应码201
    }

    //http://api.leyou.com/api/item/spec/param
    /**
     * 添加规格参数
     * @param specParam
     * @return
     */
    @PostMapping("param")
    public ResponseEntity<Void> addSpecParam(@RequestBody SpecParam specParam){
        specService.addSpecParam(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();//返回响应码201
    }

    //http://api.leyou.com/api/item/spec/param
    /**
     * 根据 规格参数id 修改 规格参数
     * @param specParam
     * @return
     */
    @PutMapping("param")
    public ResponseEntity<Void> updataSpecParam(@RequestBody SpecParam specParam){
        specService.updataSpecParam(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();//返回响应码201
    }

    //http://api.leyou.com/api/item/spec/param/33
    @DeleteMapping("param/{pid}")
    public ResponseEntity<Void> delSpecParam(@PathVariable("pid") Long pid){
        SpecParam specParam = new SpecParam();
        specParam.setId(pid);
        specService.delSpecParam(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();//返回响应码201
    }

}
