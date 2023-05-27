package cn.wolfcode.web.msg;

import cn.wolfcode.common.web.CodeMsg;

public class AddressCodeMsg extends CodeMsg {
    private AddressCodeMsg(Integer code,String msg){
        super(code, msg);
    }

    public static final AddressCodeMsg ADDRESS_OUT_OF_NUM = new AddressCodeMsg(4100,"收货地址数量已达上限");
    public static final AddressCodeMsg ADD_ADDRESS_FAIL = new AddressCodeMsg(4101,"添加收货地址失败");
    public static final AddressCodeMsg UPDATE_ADDRESS_FAIL = new AddressCodeMsg(4103,"更新收货地址失败");
    public static final AddressCodeMsg ADD_ADDRESS_SUCCESS = new AddressCodeMsg(4104,"添加收货地址成功");
    public static final AddressCodeMsg CHANGE_ADDRESS_SUCCESS = new AddressCodeMsg(4105,"收货地址修改成功");
}
