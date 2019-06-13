package cn.jzy.cart.controller;

import cn.jzy.cart.service.CartService;
import cn.jzy.pojogroup.Cart;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: 林俊豪
 * @Date: 2019/6/13
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference(timeout=6000)
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @RequestMapping("/findCartList")
    private List<Cart> findCartList(){
        // 当前登陆人账号
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录人:" + username);

        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartListString == null || cartListString.equals("")) {
            cartListString = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);

        // 表示 未登录   如果未登录从cookie中提取购物车
        if (username.equals("anonymousUser")) {
            System.out.println("从cookie提取购物车");

            return cartList_cookie;
        } else { // 如果登陆了
            // redis中提取
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
            // 当本地购物车  存在数据 才进行合并
            if (cartList_cookie.size() > 0) {
                // 得到合并后的购物车
                List<Cart> cartList = cartService.mergeCartList(cartList_cookie, cartList_redis);
                // 把合并后的购物车 存入redis
                cartService.saveCartListToRedis(username, cartList);
                // 清除本地购物车
                util.CookieUtil.deleteCookie(request, response, "cartList");
                System.out.println("执行了合并购物车");
                return cartList;
            }

            return cartList_redis;
        }
    }


    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num){
// 当前登陆人账号
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录人:" + name);

        try {
            // 提取购物车
            List<Cart> cartList = findCartList();
            // 调用服务层方法 操作 购物车
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);

            // 如果用户没有登陆
            if (name.equals("anonymousUser")) {
                System.out.println("向cookie存储购物车");
                // 把购物车  存回 cookie
                String cartListString = JSON.toJSONString(cartList);
                // 3600 * 24 一天   单位为秒
                util.CookieUtil.setCookie(request, response, "cartList", cartListString, 3600 * 24, "UTF-8");

            } else {
                cartService.saveCartListToRedis(name, cartList);
            }
            return new Result(true, "存入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "存入购物车失败");
        }
    }
}
