package cn.wolfcode.service;

import cn.wolfcode.domain.Register;

public interface IRegisterService {

    /**
     * 注册用户
     * @param register 用户信息
     */
    void register(Register register);
}
