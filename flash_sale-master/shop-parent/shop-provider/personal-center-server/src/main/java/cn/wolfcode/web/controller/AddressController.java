package cn.wolfcode.web.controller;


import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.Address;
import cn.wolfcode.service.IAddressService;
import cn.wolfcode.web.msg.AddressCodeMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private IAddressService addressService;

    @RequestMapping("/add")
    public Result<AddressCodeMsg> addAddress(Address address,Integer uid){
        addressService.addAddress(address,uid);
        return Result.success(AddressCodeMsg.ADD_ADDRESS_SUCCESS);
    }
    @RequestMapping("/change")
    public Result<AddressCodeMsg> changeAddress(Address address,Integer uid){
        addressService.change(address,uid);
        return Result.success(AddressCodeMsg.CHANGE_ADDRESS_SUCCESS);
    }

}
