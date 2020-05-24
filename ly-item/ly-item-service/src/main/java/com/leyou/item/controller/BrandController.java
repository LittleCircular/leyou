package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")

public class BrandController {

    @Autowired
    private BrandService brandService;

    ///page?page=1&rows=5&sortBy=id&desc=false&key=
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> pageQuery(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                                       @RequestParam(value = "rows",defaultValue = "10") Integer rows,
                                                       @RequestParam(value = "sortBy",required = false) String sortBy,
                                                       @RequestParam(value = "desc",required = false) Boolean desc,
                                                       @RequestParam(value = "key",required = false) String key){
        PageResult<Brand> pageResult = brandService.pageQuery(page,rows,sortBy,desc,key);
        if (pageResult != null && pageResult.getItems() != null && pageResult.getItems().size() != 0){
            return ResponseEntity.ok(pageResult);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();//返回响应码200，数据
    }

    /**
     * 添加品牌
     * @param brand
     * @param cids
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addBrand(Brand brand, @RequestParam("cids") List<Long> cids){
        brandService.addBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();//返回响应码201
    }

    @PutMapping
    public ResponseEntity<Void> updateBrand(Brand brand, @RequestParam("cids") List<Long> cids){
        brandService.updateBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();//返回响应码201
    }

    //http://api.leyou.com/api/item/brand/cid/76

    /**
     * 根据分类的id查询分类对应品牌的信息
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCategory(@PathVariable("cid") Long cid){
        List<Brand> brandList = brandService.queryBrandByCategory(cid);
        if (null != brandList && brandList.size() > 0){
            return ResponseEntity.ok(brandList);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据品牌的id查询对应品牌的信息
     * @param bid
     * @return
     */
    @GetMapping("bid/{bid}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("bid") Long bid){
        System.out.println("断断断" + bid);
        Brand brand = brandService.queryBrandById(bid);
        System.out.println("断断断" + brand);
        if (null == brand) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(brand);
    }
}
