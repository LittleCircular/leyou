package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableDiscoveryClient//开启Eureka客户端发现功能
@EnableZuulProxy//开启网关zuul
public class LyApiGateway {
    public static void main(String[] args) {
        SpringApplication.run(LyApiGateway.class,args);
    }
}
