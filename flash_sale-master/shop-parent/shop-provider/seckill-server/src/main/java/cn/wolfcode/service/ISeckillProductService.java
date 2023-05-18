package cn.wolfcode.service;

import cn.wolfcode.domain.SeckillProduct;
import cn.wolfcode.domain.SeckillProductVo;

import java.util.List;

/**
 * Created by lanxw
 */
public interface ISeckillProductService {
    /**
     * 查询秒杀列表的数据
     */
    List<SeckillProductVo> queryByTime(Integer time);

    /**
     * 根据秒杀场次和秒伤商品id来获取商品详情的方法
     * @param time 场次
     * @param secKillId 秒杀商品id
     * @return
     */
    SeckillProductVo find(Integer time, Long secKillId);

    /**
     * 秒杀库存减1
     * @param id 秒杀商品id
     * @return 受影响行数
     */
    int decrStockCount(Long id);

    /**
     * 从Redis缓存中获取秒杀商品列表
     * @param time 场次
     * @return
     */
    List<SeckillProductVo> queryByTimeFromCache(Integer time);

    /**
     * 从Redis缓存中获取秒杀商品详情
     * @param time
     * @param secKillId
     * @return
     */
    SeckillProductVo findFromCache(Integer time, Long secKillId);

    /**
     * 查询数据库的某个商品的库存并将其同步到Redis中
     * @param time 商品秒杀场次
     * @param seckillId 商品秒杀ID
     */
    void synStockToRedis(Integer time, Long seckillId);


    /**
     * 因超时取消订单之后，真实库存回补
     * @param seckillId 秒杀商品ID
     */
    void incrStockCount(Long seckillId);
}
