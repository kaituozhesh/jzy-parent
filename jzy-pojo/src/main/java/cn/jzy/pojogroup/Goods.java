package cn.jzy.pojogroup;

import cn.jzy.pojo.TbGoods;
import cn.jzy.pojo.TbGoodsDesc;
import cn.jzy.pojo.TbItem;

import java.io.Serializable;
import java.util.List;

/**
 * 商品组合实体类
 * @Auther: 林俊豪
 * @Date: 2019/4/14
 */
public class Goods implements Serializable {
    private TbGoods goods;  // 商品SPU基本西悉尼
    private TbGoodsDesc goodsDesc; // 商品SPU扩展信息
    private List<TbItem> itemList; // SKU列表

    public TbGoods getGoods() {
        return goods;
    }

    public void setGoods(TbGoods goods) {
        this.goods = goods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<TbItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TbItem> itemList) {
        this.itemList = itemList;
    }
}
