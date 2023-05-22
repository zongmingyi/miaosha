package cn.wolfcode.web.controller;

import cn.wolfcode.common.web.Result;
import cn.wolfcode.domain.SeckillProductVo;
import cn.wolfcode.service.ISeckillProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by lanxw
 * 秒杀商品信息查询
 */
@RestController
@RequestMapping("/seckillProduct")
@Slf4j
public class SeckillProductController {
    @Autowired
    private ISeckillProductService seckillProductService;

    /**
     *
     * 线程数 500 循环次数 10 总的测试次数5000
     * 85/qps
     * redis: 网络的问题
     * 55/qps
     */
    //展示秒杀商品的方法
    @RequestMapping("/queryByTime")
    public Result<List<SeckillProductVo>> queryByTime(Integer time){
//        return Result.success(seckillProductService.queryByTime(time));
        return Result.success(seckillProductService.queryByTimeFromCache(time));
    }


    /**
     *
     * 线程数 500 循环次数 10 总的测试次数5000
     * 47/qps
     * redis
     * 795/qps
     */
    //展示秒杀商品详情的方法
    @RequestMapping("/find")
    public Result<SeckillProductVo> find(@RequestParam("time") Integer time, @RequestParam("seckillId") Long secKillId){
//        return Result.success(seckillProductService.find(time,secKillId));
        return Result.success(seckillProductService.findFromCache(time,secKillId));
    }
}
