package cn.wolfcode.Exception;

import cn.wolfcode.common.web.CodeMsg;

public class InsertFailException extends BaseException{

    public InsertFailException(CodeMsg codeMsg) {
        super(codeMsg);
    }
}
