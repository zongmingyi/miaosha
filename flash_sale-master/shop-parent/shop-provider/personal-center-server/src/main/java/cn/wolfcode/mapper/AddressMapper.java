package cn.wolfcode.mapper;

import cn.wolfcode.domain.Address;

public interface AddressMapper {
    /**
     * 根据用户id查询用户的收货地址数量
     * @param uid 用户id
     * @return 收货地址数量
     */
    Integer selectAddressCountByUID(Integer uid);

    /**
     * 用户添加收货地址
     * @param address 收货地址
     * @param uid 用户id
     * @return
     */
    Integer addAddressByUID(Address address,Integer uid);

    /**
     * 根据用户id和收货地址id修改地址
     * @param address 修改的收货地址
     * @param uid 用户id
     * @return
     */
    Integer changeAddressByUID(Address address, Integer uid);

    /**
     * 设置默认地址
     * @param aid 收货地址id
     * @param uid 用户id
     * @return
     */
    Integer setDefaultAddress(Integer aid,Integer uid);
}
