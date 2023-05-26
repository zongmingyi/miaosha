package cn.wolfcode.domain;

import cn.wolfcode.common.BaseEntity.BaseField;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class Register extends BaseField {
    private Long phone;//用户注册电话
    private String password;//密码
    private String salt;//盐值

}
