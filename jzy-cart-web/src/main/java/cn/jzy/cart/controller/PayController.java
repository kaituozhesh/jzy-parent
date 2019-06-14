package cn.jzy.cart.controller;

import cn.jzy.order.service.OrderService;
import cn.jzy.pay.service.WeixinPayService;
import cn.jzy.pojo.TbPayLog;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.IdWorker;

import java.util.HashMap;
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
    private OrderService orderService;

    @RequestMapping("/createNative")
    public Map createNative(){
        // 1. 获取当前登陆用户
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // 2. 从缓存中提取支付日志
        TbPayLog payLog = orderService.searchPayLogFromRedis(username);
        // 3. 调用微信支付接口
        if (payLog != null) {
            return weixinPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee() + "");
        } else {
            return new HashMap<>();
        }
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
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
            if (map.get("trade_state").equals("SUCCESS")) {
                result = new Result(true, "支付成功");
                // 修改订单状态
                orderService.updateOrderStatus(out_trade_no, map.get("transaction_id"));
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
                break;
            }
        }
        return result;
    }
}
