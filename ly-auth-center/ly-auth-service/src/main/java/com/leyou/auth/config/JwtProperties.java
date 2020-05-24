package com.leyou.auth.config;

import com.leyou.auth.utils.RsaUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {
//    ly:
//    jwt:
//    secret: ly@Login(Auth}*^31)&hei% # 登录校验的密钥
//        pubKeyPath: D:/rsa/rsa.pub # 公钥地址
//        priKeyPath: D:/rsa/rsa.pri # 私钥地址
//        expire: 30 # token的过期时间,单位分钟
    //给配置文件中属性赋值
    private String secret;
    private String pubKeyPath;
    private String priKeyPath;
    private Integer expire;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    private String cookieName;//: LY_TOKEN
    private Integer cookieMaxAge;//: 1800

    @PostConstruct//构造方法执行之后执行，初始化
    public void init(){
        try {
            File file = new File(pubKeyPath);
            File file1 = new File(priKeyPath);
            //如果没有公钥和私钥，产生一下
            if (!file.exists() || !file1.exists()) {
                RsaUtils.generateKey(pubKeyPath,priKeyPath,secret);
            }
            publicKey = RsaUtils.getPublicKey(pubKeyPath);
            privateKey = RsaUtils.getPrivateKey(priKeyPath);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("公钥和私钥初始化失败");
        }

    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public Integer getCookieMaxAge() {
        return cookieMaxAge;
    }

    public void setCookieMaxAge(Integer cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getPubKeyPath() {
        return pubKeyPath;
    }

    public void setPubKeyPath(String pubKeyPath) {
        this.pubKeyPath = pubKeyPath;
    }

    public String getPriKeyPath() {
        return priKeyPath;
    }

    public void setPriKeyPath(String priKeyPath) {
        this.priKeyPath = priKeyPath;
    }

    public Integer getExpire() {
        return expire;
    }

    public void setExpire(Integer expire) {
        this.expire = expire;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }
}
