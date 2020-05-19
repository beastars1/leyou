package com.leyou.user.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.item.pojo.User;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.utils.CodecUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "user:verify:phone";

    public Boolean checkData(String data, Integer type) {
        User record = new User();
        // 判断数据类型
        switch (type) {
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                throw new LyException(ExceptionEnum.INVALID_USER_DATA_TYPE);
        }

        return userMapper.selectCount(record) == 0;
    }

    public void sendCode(String phone) {
        // 生产key
        String key = KEY_PREFIX + phone;
        // 生产验证码
        String code = NumberUtils.generateCode(6);

        Map<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);

        // 发送验证码
        amqpTemplate.convertAndSend("ly.sms.exchange", "sms.verify.code", msg);

        // 保存验证码
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
    }

    public void register(User user, String code) {
        // 查询redis中的验证码
        String key = KEY_PREFIX + user.getPhone();
        String codeCache = redisTemplate.opsForValue().get(key);

        // 1.校验验证码
        if (!StringUtils.equals(code, codeCache)) {
            // 校验失败，返回
            log.error("[注册服务] {}检验验证码失败", key);
            return;
        }

        user.setId(null);
        user.setCreated(new Date());

        // 2.生产盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);

        // 3.加盐加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));

        // 4.新增用户
        int count = userMapper.insertSelective(user);

        // 5.删除缓存
        if (count == 1) {
            try {
                redisTemplate.delete(key);
            } catch (Exception e) {
                log.error("[{}] 删除redis缓存验证码:{}失败", key, code, e);
            }
        } else {
            throw new LyException(ExceptionEnum.SAVE_USER_ERROR);
        }
    }

    public User queryUser(String username, String password) {
        User record = new User();
        record.setUsername(username);
        User user = userMapper.selectOne(record);

        if (user == null) {
            throw new LyException(ExceptionEnum.USER_NOT_FIND);
        }

        // 将参数密码加盐后和数据库进行对比
        if (!StringUtils.equals(
                user.getPassword(), CodecUtils.md5Hex(password, user.getSalt()))) {
            throw new LyException(ExceptionEnum.QUERY_USER_ERROR);
        }

        return user;
    }
}
