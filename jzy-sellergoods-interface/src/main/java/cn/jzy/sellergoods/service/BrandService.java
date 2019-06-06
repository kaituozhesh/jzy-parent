package cn.jzy.sellergoods.service;

import cn.jzy.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 品牌接口
 * @Auther: 林俊豪
 * @Date: 2019/6/6
 */
public interface BrandService {
    /**
     * 返回全部品牌列表
     * @return
     */
    List<TbBrand> findAll();

    /**
     *  分页查询
     * @param pageNum  当前所在页
     * @param pageSize 每页显示记录数
     * @return
     */
    PageResult findPage(int pageNum, int pageSize);

    /**
     * 添加品牌
     * @param brand
     */
    void add(TbBrand brand);

    /**
     * 根据id查询实体
     * @param id
     * @return
     */
    TbBrand findOne(Long id);

    /**
     * 修改
     * @param brand
     */
    void update(TbBrand brand);

    /**
     * 删除
     * @param ids
     */
    void delete(Long[] ids);

    /**
     *  带条件分页查询
     * @param brand  条件对象
     * @param pageNum 当前所在页
     * @param pageSize 每页显示个数
     * @return
     */
    PageResult findPage(TbBrand brand, int pageNum, int pageSize);


    List<Map> selectOptionList();
}
