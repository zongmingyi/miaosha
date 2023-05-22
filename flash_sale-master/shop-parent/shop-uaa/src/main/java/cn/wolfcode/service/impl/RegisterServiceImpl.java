package cn.wolfcode.service.impl;

import cn.wolfcode.Exception.InsertException;
import cn.wolfcode.Exception.InsertFailException;
import cn.wolfcode.domain.Register;
import cn.wolfcode.mapper.RegisterMapper;
import cn.wolfcode.service.IRegisterService;

public class RegisterServiceImpl implements IRegisterService {

    private RegisterMapper registerMapper;
    @Override
    public void register(Register register) {
        //查询是否该手机号已被注册
        Register result = registerMapper.searchByPhone(register.getPhone());
        if(result != null){
            throw new InsertException("该手机号已被注册");
        }
        int rows = registerMapper.register(register);
        if(rows != 1){
            throw new InsertFailException("出现未知异常，注册失败");
        }
    }
}
