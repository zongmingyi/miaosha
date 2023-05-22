package cn.wolfcode.mapper;

import cn.wolfcode.domain.Register;

public interface RegisterMapper {

    /**
     * 注册方法
     * @param register 注册的信息
     * @return 受影响的行数
     */
    int register(Register register);

    /**
     * 根据电话查询其是否注册了
     * @param phone 电话
     * @return 用户
     */
    Register searchByPhone(long phone);
}
