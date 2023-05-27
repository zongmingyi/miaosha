package cn.wolfcode.exception;

import cn.wolfcode.common.web.CodeMsg;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class AddressOutOfIndexException extends RuntimeException{

    private CodeMsg codeMsg;
    public AddressOutOfIndexException(CodeMsg codeMsg){
        this.codeMsg = codeMsg;
    }
}
