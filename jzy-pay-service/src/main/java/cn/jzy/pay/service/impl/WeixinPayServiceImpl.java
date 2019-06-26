package cn.jzy.pay.service.impl;

import cn.jzy.pay.service.WeixinPayService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Value;
import util.HttpClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: 林俊豪
 * @Date: 2019/6/14
 */
@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;


    @Override
    public Map createNative(String out_trade_no, String total_fee) {

        // 1. 参数封装
        Map param = new HashMap();
        // 公众账号ID  **appid
        param.put("appid", appid);
        // 商务号 ** partner
        param.put("mch_id", partner);
        // 随机字符串
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        // 商品描述
        param.put("body","金芝源");
        // 商务订单号
        param.put("out_trade_no", out_trade_no);
        // 标价金额  单位(分)
        param.put("total_fee", total_fee);
        // 终端IP
        param.put("spbill_create_ip","127.0.0.1");
        // 回调地址  使用的是方法二  所以  回调地址只要是一个合法的http地址就行
        param.put("notify_url","http://www.jzy.cn");
        // 交易类型
        param.put("trade_type","NATIVE");

        try {
            // ** partnerkey
            String paramXml = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("请求的参数 : " + paramXml);

            // 2. 发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            httpClient.post();

            // 3. 获取结果
            String xmlResult = httpClient.getContent();
            Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlResult);

            System.out.println("微信返回结果 : " + mapResult);

            Map map = new HashMap<>();
            // 生成支付二维码的链接
            map.put("code_rul", mapResult.get("code_url"));
            map.put("out_trade_no", out_trade_no);
            map.put("total_fee", total_fee);

            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    @Override
    public Map queryPayStatus(String out_trade_no) {

        // 1. 封装参数
        Map param = new HashMap();
        param.put("appid", appid);
        param.put("mch_id", partner);
        param.put("out_trade_no", out_trade_no);
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
            String paramXml = WXPayUtil.generateSignedXml(param, partnerkey);

            // 2. 发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            httpClient.post();

            // 3. 获取结果
            String xmlResult = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(xmlResult);
            System.out.println("调用查询API返回结果 : " + xmlResult);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
