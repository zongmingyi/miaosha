package cn.wolfcode.web.feign;


import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.Product;
import cn.wolfcode.web.feign.fallback.ProductFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 秒杀服务远程调用接口
 */
@FeignClient(name = "product-service",fallback = ProductFeignFallback.class)
public interface ProductFeignApi {

    /**
     *
     * @param productIds 商品ID集合
     * @return 返回Result的目的是不让被调用方在返回时做类型的判断，因为出现异常时返回的是Result类型
     */
    @RequestMapping("/product/queryByIds")
    Result<List<Product>> queryByIds(@RequestParam List<Long> productIds);


}
