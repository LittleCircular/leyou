package com.leyou.gateway.filter;

import com.leyou.auth.utils.JwtUtils;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.leyou.utils.CookieUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    private FilterProperties filterProperties;

    @Autowired
    private JwtProperties jwtProperties;

    //过滤类型
    @Override
    public String filterType() {
        //请求之前过滤
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        //请求头之前，查看请求参数
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;
    }

    //是否开启过滤
    @Override
    public boolean shouldFilter() {

        //在白名单里的过滤

        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        //获取用户请求的uri
        String requestURI = request.getRequestURI();
        //获取白名单
        List<String> allowPaths = filterProperties.getAllowPaths();
        for (String allowPath : allowPaths) {
            if (requestURI.contains(allowPath)) {
                return false;
            }
        }

        return true;
    }

    //执行代码
    @Override
    public Object run() throws ZuulException {

        //从 请求中获取cookie，然后进行解密
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();

        try {
            //获取cookie中的token
            String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
            //解密
            JwtUtils.getInfoFromToken(token,jwtProperties.getPublicKey());
            //解密成功，已登录，放行
            //解密失败，未登录，转发到登录页面
        }catch (Exception e){
            //拦截
            //不响应
            requestContext.setSendZuulResponse(false);
            //返回响应码
            requestContext.setResponseStatusCode(401);
        }
        return null;
    }
}
