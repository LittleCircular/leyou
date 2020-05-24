package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class UploadService {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    public String uploadImage(MultipartFile file) {
        String url = null;

//        try {
//            //保存图片
//            //1. 生成保存目录
//            File dir = new File("d:\\upload");
//            //如果没有upload文件夹 创建
//            if (!dir.exists()){
//                dir.mkdirs();
//            }
//            //保存图片
//            file.transferTo(new File(dir,file.getOriginalFilename()));
//
//            //拼接图片地址
//            url = "http://image.leyou.com/" + file.getOriginalFilename();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //获取文件名称
        String filename = file.getOriginalFilename();
        //获取文件名的后缀
        String ext = StringUtils.substringAfterLast(filename,".");
        try {
            StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);
            return "http://image.leyou.com/" + storePath.getFullPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return url;
    }
}
