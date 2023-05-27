package cn.wolfcode.service;

import cn.wolfcode.domain.Address;

public interface IAddressService {


    void addAddress(Address address,Integer uid);


    void change(Address address,Integer uid);
}
