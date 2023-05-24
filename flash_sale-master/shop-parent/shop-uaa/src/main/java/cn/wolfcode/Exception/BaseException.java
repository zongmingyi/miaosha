package cn.wolfcode.Exception;

import cn.wolfcode.common.web.CodeMsg;
import lombok.Getter;
import lombok.Setter;

@Setter@Getter
public class BaseException extends RuntimeException{

    private CodeMsg codeMsg;
    public BaseException(CodeMsg codeMsg){
        this.codeMsg = codeMsg;
    }
}
