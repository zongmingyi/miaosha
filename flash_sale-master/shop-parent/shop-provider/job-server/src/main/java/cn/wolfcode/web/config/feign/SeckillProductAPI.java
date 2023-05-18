package cn.wolfcode.web.config.feign;

import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.SeckillProductVo;
import cn.wolfcode.web.config.feign.fallback.SeckillProductFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "seckill-service",fallback = SeckillProductFeignFallback.class)
public interface SeckillProductAPI {
    @RequestMapping("/seckillProduct/queryByTimeForJob")
    Result<List<SeckillProductVo>> queryByTimeForJob(@RequestParam("time")Integer time);
}
