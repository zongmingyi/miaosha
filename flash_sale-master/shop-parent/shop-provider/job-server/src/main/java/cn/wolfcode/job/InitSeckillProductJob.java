package cn.wolfcode.job;


import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.SeckillProductVo;
import cn.wolfcode.redis.JobRedisKey;
import cn.wolfcode.web.config.feign.SeckillProductAPI;
import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Getter
@Setter
public class InitSeckillProductJob implements SimpleJob {
    @Value("${jobCron.initSeckillProduct}")
    private String cron;
    @Resource
    private SeckillProductAPI seckillProductAPI;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public void execute(ShardingContext shardingContext) {
        //远程调用秒杀服务获取秒杀列表集合
        String time = shardingContext.getShardingParameter();
        Result<List<SeckillProductVo>> result = seckillProductAPI.queryByTimeForJob(Integer.valueOf(time));
        if(null == result || result.hasError()){
            return;
        }
        List<SeckillProductVo> seckillProductVoList = result.getData();
        //删除之前的数据
        //拼接之后：key就是SECKILL_PRODUCT_HASH：time
        String key = JobRedisKey.SECKILL_PRODUCT_HASH.getRealKey(time);
        //库存数量的key
        String seckillStockCountKey = JobRedisKey.SECKILL_STOCK_COUNT_HASH.getRealKey(time);
        redisTemplate.delete(key);//先标记这个key失效，之后有一个后台线程lazy-free线程来异步释放内存
        redisTemplate.delete(seckillStockCountKey);
        //存储商品列表集合数据到Redis
        for(SeckillProductVo vo : seckillProductVoList){
            redisTemplate.opsForHash().put(key,String.valueOf(vo.getId()), JSON.toJSONString(vo));
            //将库存同步到Redis
            redisTemplate.opsForHash().put(seckillStockCountKey,String.valueOf(vo.getId()),String.valueOf(vo.getStockCount()));
        }
    }
}
