package cn.jzy.cart.service;

import cn.jzy.pojo.TbSeller;
import cn.jzy.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: 林俊豪
 * @Date: 2019/6/7
 * 认证类
 */
public class UserDetailServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("经过了UserDetail");
        // 构建一个角色列表
        List<GrantedAuthority> grantAuths = new ArrayList<>();
        grantAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        // 得到商家对象
        TbSeller seller = sellerService.findOne(username);
        if (seller != null) {
            // 审核通过的用户才能登陆
            if (seller.getStatus().equals("1")) {
                // 根据用户输入的密码  根 数据库中的对比   底层自己实现
                return new User(username, seller.getPassword(), grantAuths);
            }
        }
        return null;
    }
}
