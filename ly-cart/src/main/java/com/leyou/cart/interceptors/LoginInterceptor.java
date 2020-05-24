package com.leyou.cart.interceptors;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.cart.config.JwtProperties;
import com.leyou.utils.CookieUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.soap.Addressing;

public class LoginInterceptor extends HandlerInterceptorAdapter {

    private JwtProperties jwtProperties;

    private static final ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    //通过构造方法注入
    public LoginInterceptor(JwtProperties jwtProperties){
        this.jwtProperties = jwtProperties;
    }

    //在请求之前拦截
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        try {
            //从request里获取cookie
            String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());

            if (null == token) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;//拦截
            }

            //解密
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());

            //一个线程传递数据
            tl.set(userInfo);

            return true;
        }catch (Exception e){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;//拦截
        }
    }

    //在请求之后拦截
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清空ThreadLocal
        tl.remove();
    }

    public static UserInfo getLoginUser(){
        return tl.get();
    }

}
