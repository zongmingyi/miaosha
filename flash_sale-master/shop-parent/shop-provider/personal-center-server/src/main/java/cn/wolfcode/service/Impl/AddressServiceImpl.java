package cn.wolfcode.service.Impl;
import cn.wolfcode.domain.Address;

import cn.wolfcode.exception.AddAddressFailException;
import cn.wolfcode.exception.AddressOutOfIndexException;
import cn.wolfcode.exception.UpdateAddressException;
import cn.wolfcode.mapper.AddressMapper;
import cn.wolfcode.service.IAddressService;
import cn.wolfcode.web.msg.AddressCodeMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AddressServiceImpl implements IAddressService {

    @Autowired
    private AddressMapper addressMapper;

    /**
     * 添加收货地址
     * @param address 收货地址
     * @param uid 用户id
     */
    @Override
    public void addAddress(Address address, Integer uid) {
        Integer result = addressMapper.selectAddressCountByUID(uid);
        if(result >= 10){
            throw new AddressOutOfIndexException(AddressCodeMsg.ADDRESS_OUT_OF_NUM);
        }
        address.setAId(result + 1);
        Integer row = addressMapper.addAddressByUID(address, uid);
        if(1 != row){
            throw new AddAddressFailException(AddressCodeMsg.ADD_ADDRESS_FAIL);
        }
    }

    /**
     * 修改收货地址
     * @param address 修改的收货地址
     * @param uid 用户id
     */
    @Override
    public void change(Address address, Integer uid) {
        Integer row = addressMapper.changeAddressByUID(address, uid, address.getAId());
        if(1 != row){
            throw new UpdateAddressException(AddressCodeMsg.UPDATE_ADDRESS_FAIL);
        }
    }
}
