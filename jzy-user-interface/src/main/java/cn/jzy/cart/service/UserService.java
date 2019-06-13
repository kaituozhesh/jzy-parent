package cn.jzy.cart.service;

import java.util.List;
import cn.jzy.pojo.TbUser;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface UserService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbUser> findAll();

	/**
	 * 增加
	*/
	public void add(TbUser user);
	
	/**
	 * 修改
	 */
	public void update(TbUser user);
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbUser findOne(Long id);

	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

    /**
     * 发送短信验证码
     * @param phone
     */
	void createSmsCode(String phone);

    /**
     *  校验  验证码
     * @param phone
     * @param code
     * @return
     */
	boolean checkSmsCode(String phone, String code);
}
