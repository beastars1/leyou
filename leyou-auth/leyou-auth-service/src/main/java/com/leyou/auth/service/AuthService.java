package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties prop;

    public String accredit(String username, String password) {
        // 1.根据用户名和密码查询
        User user = userClient.queryUser(username, password);

        // 2.判断user
        if (user == null) {
            log.error("[授权服务] 用户信息不存在，{}", username);
            throw new LyException(ExceptionEnum.QUERY_USER_ERROR);
        }

        try {
            // 3.jwtUtils生产jwt类型的token
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());

            return JwtUtils.generateToken(userInfo, prop.getPrivateKey(), prop.getExpire());
        } catch (Exception e) {
            log.error("[授权服务] 授权失败", e);
            return null;
        }
    }
}
