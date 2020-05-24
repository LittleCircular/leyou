package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserClient userClient;

    public String accredit(String username, String password) {
        try {
            User user = userClient.queryUser(username, password);
            if (null == user) {
                return null;
            }
            //产生token
            UserInfo userInfo = new UserInfo(user.getId(), user.getUsername());
            String token = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getCookieMaxAge());
            return token;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
