package cn.wolfcode.service.impl;

import cn.wolfcode.Exception.InsertException;
import cn.wolfcode.Exception.InsertFailException;
import cn.wolfcode.Exception.PhoneIsNullException;
import cn.wolfcode.domain.Register;
import cn.wolfcode.mapper.RegisterMapper;
import cn.wolfcode.service.IRegisterService;
import cn.wolfcode.util.MD5Util;
import cn.wolfcode.web.msg.RegisterCodeMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

@Service
public class RegisterServiceImpl implements IRegisterService {

    @Autowired
    private RegisterMapper registerMapper;
    @Override
    public int register(Register register) {
        //判断手机号是否为空
        if(null == register.getPhone()){
            throw new PhoneIsNullException(RegisterCodeMsg.PHONE_IS_NULL);
        }
        //查询是否该手机号已被注册

        Register result = registerMapper.searchByPhone(register.getPhone());
        if(result != null){
            throw new InsertException(RegisterCodeMsg.DUPLICATE_REGISTER);
        }
        //加密密码
        //生成盐值
        String randomUUID = UUID.randomUUID().toString().toUpperCase();
        String MD5Password = MD5Util.encode(register.getPassword(), randomUUID);
        register.setPassword(MD5Password);
        register.setSalt(randomUUID);
        //将剩余的字段补充
        register.setCreateUser(String.valueOf(register.getPhone()));
        register.setCreateTime(new Date());
        register.setModifyUser(String.valueOf(register.getPhone()));
        register.setModifyTime(new Date());

        int rows = registerMapper.register(register);
        if(rows != 1){
            throw new InsertFailException(RegisterCodeMsg.REGISTER_FAIL);
        }
        return rows;
    }
}
