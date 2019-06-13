package cn.jzy.cart.service.impl;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jzy.cart.service.UserService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import cn.jzy.mapper.TbUserMapper;
import cn.jzy.pojo.TbUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbUser> findAll() {
		return userMapper.selectByExample(null);
	}

	/**
	 * 增加   888888
	 */
	@Override
	public void add(TbUser user) {
	    // 用户注册时间
	    user.setCreated(new Date());
	    user.setUpdated(new Date());
	    user.setSourceType("1"); // 注册来源
        // md5 密码加密
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));
		userMapper.insert(user);		
	}
	
	/**
	 * 修改
	 */
	@Override
	public void update(TbUser user){
		userMapper.updateByPrimaryKey(user);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbUser findOne(Long id){
		return userMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			userMapper.deleteByPrimaryKey(id);
		}		
	}

	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private JmsTemplate jmsTemplate;
    @Autowired
	private Destination smsDestination;
    @Value("${template_code}")
    private String template_code;
    @Value("${sign_name}")
    private String sign_name;
    @Override
    public void createSmsCode(final String phone) {
        // 1. 生产一个六位验证码
        final String smscode = (long)(Math.random() * 900000 + 100000) + "";
        System.out.println("验证码 : " + smscode);
        // 2. 将验证码放入缓存
        redisTemplate.boundHashOps("smscode").put(phone, smscode);
        // 3. 将短信内容发送给activeMQ
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage message = session.createMapMessage();
                message.setString("mobile",phone);
                message.setString("template_code",template_code); // 验证码
                message.setString("sign_name",sign_name); // 签名
                Map map = new HashMap();
                map.put("number",smscode);
                message.setString("param", JSON.toJSONString(map));
                return message;
            }
        });
    }

    @Override
    public boolean checkSmsCode(String phone, String code) {
        String systemcode = (String) redisTemplate.boundHashOps("smscode").get(phone);
        System.out.println("Redis : " + systemcode);
        if (systemcode == null) {
            return false;
        }
        if (!systemcode.equals(code)) {
            return false;
        }
        return true;
    }


}
