package com.leyou;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;
import sun.plugin2.message.StopAppletMessage;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    //公钥路径
    private static final String pubKeyPath = "d:\\temp\\rsa.pub";
    //私钥路径
    private static final String priKeyPath = "d:\\temp\\rsa.pri";

    //公钥对象和私钥对象（java自带）
    private PublicKey publicKey;
    private PrivateKey privateKey;

//    @Test
//    public void test1() throws Exception{
//        //公钥路径，私钥路径，密钥
//        RsaUtils.generateKey(pubKeyPath,priKeyPath,"1234");
//    }

    @Before
    public void load() throws Exception{
        //读取公钥
        publicKey = RsaUtils.getPublicKey(pubKeyPath);
        //读取私钥
        privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test//加密
    public void test2() throws Exception{
        //创建载荷对象
        UserInfo userInfo = new UserInfo(100L, "tom");
        //加密
        //使用工具方法 ， 参数（载荷，私钥，存活时间）
        String token = JwtUtils.generateToken(userInfo, privateKey, 5);
        System.out.println(token);
    }

    @Test//解密
    public void test3() throws Exception{
        String s = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MTAwLCJ1c2VybmFtZSI6InRvbSIsImV4cCI6MTU4OTQ1ODEyM30.gOLKxDN9aW4asuVPr3xgCLwXYAZv7q2aqgG9pf0eSL876esxfabZCBmBK8-68vTgepJbiwk4SK2NbxIsl0grSJ6w84yGlUNUVrn677UBDrhXE6nBv3_AShnNPtGCkbfb4Dar58byGbr5bEKNeWJvHSrtqzkEXgpYJ2dJ6f14oBg";
        //解密（需要解密的字符串，公钥）
        UserInfo userInfo = JwtUtils.getInfoFromToken(s, publicKey);
        System.out.println(userInfo.getUsername());
        System.out.println(userInfo.getId());
    }
}
