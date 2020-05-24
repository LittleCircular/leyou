package com.leyou.listener;

import com.leyou.service.FileService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsListener {

    @Autowired
    private FileService fileService;

    //接收消息
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "q3",durable = "true"),
            exchange = @Exchange(value = "ly.item.exchange",type = ExchangeTypes.TOPIC),
            key = {"item.update","item.insert"}
    ))
    public void listenCreate(Long id){
        //重新查询数据库，产生静态页面
        fileService.syncCreateHtml(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "q4",durable = "true"),
            exchange = @Exchange(value = "ly.item.exchange",type = ExchangeTypes.TOPIC),
            key = {"item.delete"}
    ))
    public void listenDelete(Long id){
        //删除静态页面
        fileService.deleteHtml(id);
    }

}
