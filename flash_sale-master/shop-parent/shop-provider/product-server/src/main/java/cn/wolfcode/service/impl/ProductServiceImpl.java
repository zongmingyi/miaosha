package cn.wolfcode.service.impl;

import cn.wolfcode.domain.Product;
import cn.wolfcode.mapper.ProductMapper;
import cn.wolfcode.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * Created by lanxw
 */
@Service
public class ProductServiceImpl implements IProductService {
    @Resource
    private ProductMapper productMapper;

    @Override
    public List<Product> queryByIds(List<Long> productIds) {
        if(null == productIds || productIds.size() == 0){
            return Collections.EMPTY_LIST;
        }
        return productMapper.queryProductByIds(productIds);
    }
}
