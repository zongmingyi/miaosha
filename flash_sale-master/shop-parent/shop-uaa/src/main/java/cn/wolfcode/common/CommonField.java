package cn.wolfcode.common;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter@Setter
public class CommonField {
    private String createUser;//创建者
    private Date createTime;//创建时间
    private String modifyUser;//修改者
    private Date modifyTime;//修改时间
}
