package cn.wolfcode.domain;

import cn.wolfcode.common.CommonField;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter@Setter
public class register extends CommonField {
    private Long id;//自增id
    private Long phone;//登陆用户的手机号码
    private String password;//登录IP
    private Date loginTime;///登录时间
    private Boolean state;//登录状态
}
