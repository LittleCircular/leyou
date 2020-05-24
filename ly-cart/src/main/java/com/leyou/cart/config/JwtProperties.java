package com.leyou.cart.config;

import com.leyou.auth.utils.RsaUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {

    private String pubKeyPath;

    private PublicKey publicKey;

    private String cookieName;//: LY_TOKEN

    @PostConstruct//构造方法执行之后执行，初始化
    public void init(){
        try {

            publicKey = RsaUtils.getPublicKey(pubKeyPath);

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("公钥和私钥初始化失败");
        }

    }

    public String getPubKeyPath() {
        return pubKeyPath;
    }

    public void setPubKeyPath(String pubKeyPath) {
        this.pubKeyPath = pubKeyPath;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }
}
