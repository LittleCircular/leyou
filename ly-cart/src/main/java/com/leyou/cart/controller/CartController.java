package com.leyou.cart.controller;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    //增
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //http://api.leyou.com/api/cart
    //查
    @GetMapping
    public ResponseEntity<List<Cart>> queryCarts(){
        List<Cart> carts = cartService.queryCarts();
        if (null != carts && carts.size() > 0) {
            return ResponseEntity.ok(carts);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //http://api.leyou.com/api/cart/increment
    //商品加一
    @PutMapping("increment")
    public ResponseEntity<Void> updateIncrementCart(@RequestBody Cart cart){
        cartService.updateIncrementCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //http://api.leyou.com/api/cart/15225637949
    //删除
    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") Long skuId){
        cartService.deleteCart(skuId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
