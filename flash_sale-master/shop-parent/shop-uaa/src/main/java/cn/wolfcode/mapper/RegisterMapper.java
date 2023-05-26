package cn.wolfcode.mapper;

import cn.wolfcode.domain.Register;
import org.apache.ibatis.annotations.Mapper;

@Mapper
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
    Register searchByPhone(Long phone);
}
