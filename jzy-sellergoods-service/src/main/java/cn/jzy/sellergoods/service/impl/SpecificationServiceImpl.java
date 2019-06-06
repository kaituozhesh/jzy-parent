package cn.jzy.sellergoods.service.impl;

import cn.jzy.mapper.TbSpecificationMapper;
import cn.jzy.mapper.TbSpecificationOptionMapper;
import cn.jzy.pojo.TbSpecification;
import cn.jzy.pojo.TbSpecificationExample;
import cn.jzy.pojo.TbSpecificationOption;
import cn.jzy.pojo.TbSpecificationOptionExample;
import cn.jzy.pojogroup.Specification;
import cn.jzy.sellergoods.service.SpecificationService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;

	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {
        //获取规格实体
        TbSpecification tbspecification = specification.getSpecification();
        specificationMapper.insert(tbspecification);
        //获取规格选项集合
        List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
        for( TbSpecificationOption option:specificationOptionList){
            option.setSpecId(tbspecification.getId());//设置规格ID
            specificationOptionMapper.insert(option);//新增规格
        }
    }

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){

        //修改实体
        TbSpecification tbspecification = specification.getSpecification();
        specificationMapper.updateByPrimaryKey(tbspecification);

        // 删除原来规格对应的规格选项
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(tbspecification.getId());
        specificationOptionMapper.deleteByExample(example);

        //获取规格选项集合
        List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
        for( TbSpecificationOption option:specificationOptionList){
            option.setSpecId(tbspecification.getId());//设置规格ID
            specificationOptionMapper.insert(option);//新增规格
        }
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
        Specification specification = new Specification();
        specification.setSpecification(specificationMapper.selectByPrimaryKey(id)); // 获取规格实体
        // 根据id查询
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id);
        specification.setSpecificationOptionList(specificationOptionMapper.selectByExample(example)); // 获取规格选项列表
		return specification;// 组合实体类
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
		    // 删除规格选项数据
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(id);
            specificationOptionMapper.deleteByExample(example);
		    // 删除规格数据
			specificationMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		TbSpecificationExample.Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override
    public List<Map> selectOptionList() {
        return specificationMapper.selectOptionList();
    }

}
