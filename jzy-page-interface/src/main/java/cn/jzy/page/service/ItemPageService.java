package cn.jzy.page.service;

/**
 * @Auther: 林俊豪
 * @Date: 2019/6/10
 */
public interface ItemPageService {

    /**
     * 生成商品详情页
     * @param goodsId
     * @return
     */
    boolean genItemHtml(Long goodsId);
}
