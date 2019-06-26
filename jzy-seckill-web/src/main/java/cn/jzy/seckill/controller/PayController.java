package cn.jzy.seckill.controller;


import cn.jzy.pay.service.WeixinPayService;
import cn.jzy.pojo.TbPayLog;
import cn.jzy.pojo.TbSeckillOrder;
import cn.jzy.seckill.service.SeckillOrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.channel.InsecureChannelProcessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: 林俊豪
 * @Date: 2019/6/14
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;
    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/createNative")
    public Map createNative(){
        // 1. 获取当前登陆用户
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // 2. 提取秒杀订单（从缓存）
        TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(username);
        // 3. 调用微信支付接口
        if (seckillOrder != null) {
            System.out.println("进入   ");
            return weixinPayService.createNative(seckillOrder.getId() + "", (long)(seckillOrder.getMoney().doubleValue() * 100) + "");
        } else {
            return new HashMap<>();
        }
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        // 1. 获取当前登陆用户
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Result result = null;
        int x = 0;
        while (true) {
            // 调用查询
            Map<String, String> map = weixinPayService.queryPayStatus(out_trade_no);
            if (map == null) {
                result = new Result(false, "支付发生错误");
                break;
            }
            // 支付成功
            System.out.println("TRADE_STATU : " + map.get("trade_state"));
            if(map.get("trade_state").equals("SUCCESS")){//支付成功
                result=new Result(true, "支付成功");
                //保存订单
                seckillOrderService.saveOrderFromRedisToDb(username, Long.valueOf(out_trade_no) ,map.get("transaction_id"));
                break;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            x ++;
            if (x >= 100) {
                result = new Result(false, "二维码超时");
                // 关闭支付
                Map<String, String> payResult = weixinPayService.closePay(out_trade_no);
                if(payResult != null && "FAIL".equals( payResult.get("return_code"))){
                    if("ORDERPAID".equals(payResult.get("err_code"))){
                        result = new Result(true, "支付成功");
                        //保存订单
                        seckillOrderService.saveOrderFromRedisToDb(username, Long.valueOf(out_trade_no) ,map.get("transaction_id"));
                    }
                }
                //删除订单
                if(result.isSuccess()==false){
                    seckillOrderService.deleteOrderFromRedis(username, Long.valueOf(out_trade_no));
                }
                break;
            }
        }
        return result;
    }
}























