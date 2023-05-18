package cn.wolfcode.service;


import cn.wolfcode.domain.OrderInfo;
import cn.wolfcode.domain.SeckillProductVo;

import java.util.Map;

/**
 * Created by wolfcode-lanxw
 */
public interface IOrderInfoService {

    /**
     *  根据用户手机号和秒伤商品Id查询用户是否以抢购该商品
     * @param phone 电话
     * @param seckillId 秒杀商品ID
     * @return
     */
    OrderInfo findByPhoneAndSeckillId(String phone, Long seckillId);

    /**
     * 秒杀订单创建和库存数量减1的方法
     * @param phone 电话，也就是用户id
     * @param seckillProductVo 要秒杀的商品
     * @return
     */
    OrderInfo doSeckill(String phone, SeckillProductVo seckillProductVo);

    /**
     * 根据订单号查询订单信息
     * @param orderNo 订单号
     * @return
     */
    OrderInfo findByOrderInfo(String orderNo);

    /**
     * 因未支付超时而根据订单号取消订单
     * @param orderNo
     */
    void cancelOrder(String orderNo);
}
