package com.leyou.service;

import com.leyou.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

@Service
public class FileService {

    @Value("${ly.thymeleaf.destPath}")
    private String destPath;//D:/nginx-1.14.2/html/item

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private PageService pageService;

    //判断文件夹下有没有指定的html
    public boolean exist(Long spuId) {
        File file = new File(destPath);
        //判断是否存在D:/nginx-1.14.2/html/item 文件夹
        if (!file.exists()) {
            file.mkdirs();
        }
        File file1 = new File(destPath, spuId + ".html");
        return file1.exists();
    }

    public void syncCreateHtml(Long spuId) {
        //使用多线程,调用产生静态页面的方法
        ThreadUtils.execute(() -> {
            createHtml(spuId);
        });
    }

    private void createHtml(Long spuId) {
        //创建上下文对象
        Context context = new Context();
        context.setVariables(pageService.loadData(spuId));//把数据放入到上下文对象里

        File file = new File(destPath, spuId + ".html");
        //

        try {
            //创建输出流
            PrintWriter printWriter = new PrintWriter(file,"utf-8");
            //产生html
            templateEngine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void deleteHtml(Long id) {
        File file = new File(destPath,id+".html");
        file.deleteOnExit();
    }
}
