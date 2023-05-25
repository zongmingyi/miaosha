package cn.wolfcode.web.msg;

import cn.wolfcode.common.web.CodeMsg;

public class RegisterCodeMsg extends CodeMsg {
    //定义私有构造函数
    private RegisterCodeMsg(Integer code, String msg) {
        super(code, msg);
    }
    //定义常量对象
    public static final RegisterCodeMsg DUPLICATE_REGISTER = new RegisterCodeMsg(4000,"手机号已被注册");
    public static final RegisterCodeMsg PHONE_IS_NULL = new RegisterCodeMsg(4001,"手机号为空");
    public static final RegisterCodeMsg REGISTER_FAIL = new RegisterCodeMsg(4002,"注册账户失败");

}
