package com.leyou.item.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Table(name = "tb_user")
@Data
@ToString
public class User {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    @Length(min = 4, max = 30, message = "用户名长度为4-30")
    private String username;// 用户名

    @JsonIgnore
    @Length(min = 6, max = 18, message = "密码长度为6-18")
    private String password;// 密码

    @Pattern(regexp = "^1[356789]\\d{9}$", message = "手机号格式错误")
    private String phone;// 电话

    private Date created;// 创建时间

    @JsonIgnore
    private String salt;// 密码的盐值
}