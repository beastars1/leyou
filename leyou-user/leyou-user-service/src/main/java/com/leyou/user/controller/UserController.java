package com.leyou.user.controller;

import com.leyou.item.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 校验数据
     *
     * @param data
     * @param type
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(
            @PathVariable(value = "data") String data, @PathVariable(value = "type") Integer type) {
        return ResponseEntity.ok(userService.checkData(data, type));
    }

    /**
     * 发送短信
     *
     * @param phone
     * @return
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendCode(@RequestParam("phone") String phone) {
        userService.sendCode(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 用户注册
     *
     * @param user
     * @param code
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code") String code) {
        userService.register(user, code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据用户名和密码查询用户
     *
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/query")
    public ResponseEntity<User> queryUser(@RequestParam("username") String username,
                                          @RequestParam("password") String password) {
        return ResponseEntity.ok(userService.queryUser(username, password));
    }
}
