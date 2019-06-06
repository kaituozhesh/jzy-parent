package cn.jzy.sellergoods.service.impl;

import cn.jzy.mapper.TbItemCatMapper;
import cn.jzy.pojo.TbItemCat;
import cn.jzy.pojo.TbItemCatExample;
import cn.jzy.sellergoods.service.ItemCatService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbItemCat> findAll() {
		return itemCatMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbItemCat> page=   (Page<TbItemCat>) itemCatMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbItemCat itemCat) {
		itemCatMapper.insert(itemCat);
	}


	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat itemCat){
		itemCatMapper.updateByPrimaryKey(itemCat);
	}

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbItemCat findOne(Long id){
		return itemCatMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
        for(Long id:ids){
            delete(id);
            itemCatMapper.deleteByPrimaryKey(id);
        }
	}
    // 递归删除
	private void delete(Long id){
        List<TbItemCat> byParentId = findByParentId(id); // 根据当前id查询下一级
        for (TbItemCat t : byParentId){
            delete(t.getId());
            itemCatMapper.deleteByPrimaryKey(t.getId());
        }
    }


	@Override
	public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbItemCatExample example=new TbItemCatExample();
		TbItemCatExample.Criteria criteria = example.createCriteria();

		if(itemCat!=null){
						if(itemCat.getName()!=null && itemCat.getName().length()>0){
				criteria.andNameLike("%"+itemCat.getName()+"%");
			}

		}

		Page<TbItemCat> page= (Page<TbItemCat>)itemCatMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

    /**
     * 根据上级id查询商品列表
     * @param parentId
     * @return
     */
    @Override
    public List<TbItemCat> findByParentId(Long parentId) {
        TbItemCatExample example = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        return itemCatMapper.selectByExample(example);
    }

}
