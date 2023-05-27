package cn.wolfcode.exception;

import cn.wolfcode.common.web.CodeMsg;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class AddAddressFailException extends RuntimeException{
    private CodeMsg codeMsg;
    public AddAddressFailException(CodeMsg codeMsg){
        this.codeMsg = codeMsg;
    }
}
