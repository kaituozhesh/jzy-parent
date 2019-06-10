package cn.jzy.search.service;

import java.util.List;
import java.util.Map;

/**
 * @Auther: 林俊豪
 * @Date: 2019/6/9
 */
public interface ItemSearchService {


    /**
     * 搜索方法
     * @param searchMap
     * @return
     */
    Map search(Map searchMap);

    /**
     * 导入列表
     * @param list
     */
    void importList(List list);

    /**
     * 删除商品
     * @param goodsIds  (SPU)
     */
    void deleteByGoodsIds(List goodsIds);
}
