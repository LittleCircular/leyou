package com.leyou.user.service;

import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.utils.CodecUtils;
import com.leyou.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private StringRedisTemplate template;

    private static final String KEY_PREFIX = "user:code:phone:";

    @Autowired
    private UserMapper userMapper;

    public Boolean checkUser(String data, Integer type) {
        User user = new User();
        switch (type){
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
        }
        int i = userMapper.selectCount(user);
        return i != 1;
//        User user1 = userMapper.selectOne(user);
//        if (null == user1) {
//            return true;
//        }else {
//            return false;
//        }
    }

    public Boolean sendVerifyCode(String phone) {
        //产生验证码
        String code = NumberUtils.generateCode(5);
        //把验证码发送到手机
        try {
            ValueOperations<String, String> stringStringValueOperations = template.opsForValue();
            stringStringValueOperations.set(KEY_PREFIX+phone,code,5, TimeUnit.MINUTES);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public Boolean register(User user, String code) {
        //校验验证码
        //获取redis里的验证码
        ValueOperations<String, String> stringStringValueOperations = template.opsForValue();
        //通过用户手机号获取验证码
        String s = stringStringValueOperations.get(KEY_PREFIX + user.getPhone());
        //校验
        if (!code.equals(s)) {
            //验证码不相等
            return false;
        }

        user.setId(null);
        user.setCreated(new Date());
        //生成盐值
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);

        //密码加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));

        Boolean flag = userMapper.insertSelective(user) == 1;

        if (flag) {
            //删除redis里的验证码
            template.delete(KEY_PREFIX + user.getPhone());
        }
        return flag;
    }

    public User queryUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        //查询用户
        User user1 = userMapper.selectOne(user);

        //校验用户名,
        if (null == user1) {
            return null;
        }
        //校验密码
        if (!user1.getPassword().equals(CodecUtils.md5Hex(password,user1.getSalt()))) {
            return null;
        }

        //用户名密码都正确
        return user1;

    }
}
