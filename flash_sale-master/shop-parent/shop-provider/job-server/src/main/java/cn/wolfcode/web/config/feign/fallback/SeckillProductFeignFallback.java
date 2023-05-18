package cn.wolfcode.web.config.feign.fallback;

import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.SeckillProductVo;
import cn.wolfcode.web.config.feign.SeckillProductAPI;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeckillProductFeignFallback implements SeckillProductAPI {
    @Override
    public Result<List<SeckillProductVo>> queryByTimeForJob(Integer time) {
        return null;
    }
}
