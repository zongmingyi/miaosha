package cn.wolfcode.service.impl;

import cn.wolfcode.common.exception.BusinessException;
import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.Product;
import cn.wolfcode.domain.SeckillProduct;
import cn.wolfcode.domain.SeckillProductVo;
import cn.wolfcode.mapper.SeckillProductMapper;
import cn.wolfcode.redis.SeckillRedisKey;
import cn.wolfcode.service.ISeckillProductService;
import cn.wolfcode.web.feign.ProductFeignApi;
import cn.wolfcode.web.msg.SeckillCodeMsg;
import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by lanxw
 */
@Service
public class SeckillProductServiceImpl implements ISeckillProductService {
    @Resource
    private SeckillProductMapper seckillProductMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Resource
    private ProductFeignApi productFeignApi;
    @Override
    public List<SeckillProductVo> queryByTime(Integer time) {
        //1.查询秒杀商品集合数据，根据秒杀场次来查询
        List<SeckillProduct> seckillProducts = seckillProductMapper.queryCurrentlySeckillProduct(time);
        if(0 == seckillProducts.size()){
            return Collections.EMPTY_LIST;
        }
        //2.遍历秒杀商品集合数据，获得商品数据id集合
        List<Long> productIds = new ArrayList<>();
        for (SeckillProduct seckillProduct : seckillProducts) {
            productIds.add(seckillProduct.getProductId());
        }

        //3.远程调用商品微服务，获取商品信息集合
        Result<List<Product>> result = productFeignApi.queryByIds(productIds);

        if(null == result||result.hasError()) {
            throw  new BusinessException(SeckillCodeMsg.PRODUCT_SERVER_ERROR);
        }

        List<Product> productList = result.getData();
        //商品id和商品的键值对，方便查询
        Map<Long,Product> productMap = new HashMap<>();
        for (Product product : productList) {
            productMap.put(product.getId(),product);
        }

        //3.将商品和秒杀商品数据集合，封装Vo并返回
        List<SeckillProductVo> seckillProductVoList = new ArrayList<>();
        for (SeckillProduct seckillProduct: seckillProducts){
            SeckillProductVo vo = new SeckillProductVo();
            Product product = productMap.get(seckillProduct.getProductId());
            //利用工具类的方法进行拷贝
            BeanUtils.copyProperties(product,vo);
            //因为有的是秒杀商品的id,所以秒杀商品集合放在之后进行覆盖
            BeanUtils.copyProperties(seckillProduct,vo);

            vo.setCurrentCount(seckillProduct.getStockCount());//当前默认数据等于库存数量
            seckillProductVoList.add(vo);
        }

        return seckillProductVoList;
    }

    @Override
    public SeckillProductVo find(Integer time, Long secKillId) {
        //查询秒杀商品对象
        SeckillProduct seckillProduct = seckillProductMapper.find(secKillId);
        //根据id查询商品对象
        //复用一下远程调用方法
        List<Long> productIds = new ArrayList<>();
        productIds.add(seckillProduct.getProductId());
        Result<List<Product>> result = productFeignApi.queryByIds(productIds);
        if(null == result || result.hasError()){
            throw new BusinessException(SeckillCodeMsg.PRODUCT_SERVER_ERROR);
        }
        Product product = result.getData().get(0);
        //将数据封装vo对象
        SeckillProductVo seckillProductVo = new SeckillProductVo();
        BeanUtils.copyProperties(product,seckillProductVo);
        BeanUtils.copyProperties(seckillProduct,seckillProductVo);
        seckillProductVo.setCurrentCount(seckillProduct.getStockCount());
        return seckillProductVo;
    }

    @Override
    public int decrStockCount(Long id) {
        return seckillProductMapper.decrStock(id);
    }

    @Override
    public List<SeckillProductVo> queryByTimeFromCache(Integer time) {
        //获得存储在redis中真正的key值
        String key = SeckillRedisKey.SECKILL_PRODUCT_HASH.getRealKey(String.valueOf(time));
        //获得key对应的value值
        List<Object> list = redisTemplate.opsForHash().values(key);
        List<SeckillProductVo> seckillProductVoList = new ArrayList<>();
        for (Object obj : list) {
            //将从value中读取到的value值强转成string，再转成SeckillProductVo类型，之后将其添加到商品列表中
            seckillProductVoList.add(JSON.parseObject((String) obj,SeckillProductVo.class));
        }
        return seckillProductVoList;
    }

    @Override
    public SeckillProductVo findFromCache(Integer time, Long secKillId) {
        String key = SeckillRedisKey.SECKILL_PRODUCT_HASH.getRealKey(String.valueOf(time));
        //这个相当于就是求对应key值列表中，对应seckillId的felid中的value
        Object obj = redisTemplate.opsForHash().get(key, String.valueOf(secKillId));
        SeckillProductVo vo = JSON.parseObject((String) obj,SeckillProductVo.class);
        return vo;
    }

    @Override
    public void synStockToRedis(Integer time, Long seckillId) {
        SeckillProduct seckillProduct = seckillProductMapper.find(seckillId);
        //库存大于0，才进行Redis的预库存同步
        if(seckillProduct.getStockCount() > 0){
            //秒杀场次库存的key  orderStockCount；time
            String key = SeckillRedisKey.SECKILL_STOCK_COUNT_HASH.getRealKey(String.valueOf(time));
            redisTemplate.opsForHash().put(key,String.valueOf(seckillId),String.valueOf(seckillProduct.getStockCount()));
        }
    }

    @Override
    public void incrStockCount(Long seckillId) {
        seckillProductMapper.incrStock(seckillId);
    }
}
