package cn.wolfcode.domain;

import cn.wolfcode.common.CommonField;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter@Setter
public class Register extends CommonField {
    private Long phone;//用户注册电话
    private String password;//密码
    private String salt;//盐值

}
