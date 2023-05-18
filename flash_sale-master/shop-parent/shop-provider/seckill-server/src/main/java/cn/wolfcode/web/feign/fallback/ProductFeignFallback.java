package cn.wolfcode.web.feign.fallback;

import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.Product;
import cn.wolfcode.web.feign.ProductFeignApi;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *fallback类是为了在服务调用失败或超时的情况下，通过备用方法的方式处理异常情况。
 */
@Component
public class ProductFeignFallback implements ProductFeignApi {
    @Override
    public Result<List<Product>> queryByIds(List<Long> productIDs) {
        //返回兜底数据以应对服务调用失败的情况
        System.out.println("远程服务调用失败");
        return null;
    }
}
