package com.leyou.cart.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.auth.entity.UserInfo;
import com.leyou.cart.interceptors.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate template;

    private static final String KEY_PREFIX = "ly:cart:uid:";

    public void addCart(Cart cart) {
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        BoundHashOperations<String, Object, Object> boundHashOperations = template.boundHashOps(KEY_PREFIX + userInfo.getId());
        Object o = boundHashOperations.get(cart.getSkuId().toString());
        if (null != o) {//有值（有该商品）。数量加1
            //json格式字符串变成cart对象
            Cart cart1 = JsonUtils.nativeRead(o.toString(), new TypeReference<Cart>() {
            });

            cart1.setNum(cart1.getNum() + cart.getNum());

            //更新后的数据放入redis
            boundHashOperations.put(cart.getSkuId().toString(),JsonUtils.serialize(cart1));

        }else {//没有值（没有该商品）加入redis
            //把cart变成json格式字符串
            String c = JsonUtils.serialize(cart);

            boundHashOperations.put(cart.getSkuId().toString(),c);
        }
    }

    public List<Cart> queryCarts() {
        //根据用户id查询redis
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getLoginUser();

        BoundHashOperations<String, Object, Object> boundHashOperations = template.boundHashOps(KEY_PREFIX + userInfo.getId());
        List<Object> values = boundHashOperations.values();

        List<Cart> carts = new ArrayList<>();

        if (null != values) {
            for (Object value : values) {
                //把value变成cart
                Cart cart = JsonUtils.nativeRead(value.toString(), new TypeReference<Cart>() {
                });
                carts.add(cart);
            }
        }
        return carts;
    }

    public void updateIncrementCart(Cart cart) {
        //根据用户id查询redis
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getLoginUser();

        BoundHashOperations<String, Object, Object> boundHashOperations = template.boundHashOps(KEY_PREFIX + userInfo.getId());
        String s = boundHashOperations.get(cart.getSkuId().toString()).toString();
        //有值（有该商品）。数量加1
        //json格式字符串变成cart对象
        Cart cart1 = JsonUtils.nativeRead(s, new TypeReference<Cart>() {
        });

        cart1.setNum(cart1.getNum() + 1);

        //更新后的数据放入redis
        boundHashOperations.put(cart.getSkuId().toString(),JsonUtils.serialize(cart1));
    }

    public void deleteCart(Long skuId) {
        //删除redis中的一条数据
        //根据用户id查询redis
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getLoginUser();

        BoundHashOperations<String, Object, Object> boundHashOperations = template.boundHashOps(KEY_PREFIX + userInfo.getId());

        boundHashOperations.delete(String.valueOf(skuId));
    }
}
