package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "tb_brand")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '品牌id',
    private String name;//` varchar(50) NOT NULL COMMENT '品牌名称',
    private String image;//` varchar(200) DEFAULT '' COMMENT '品牌图片地址',
    private Character letter;//` char(1) DEFAULT '' COMMENT '品牌的首字母',
}
