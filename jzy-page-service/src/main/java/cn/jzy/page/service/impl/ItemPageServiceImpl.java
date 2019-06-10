package cn.jzy.page.service.impl;

import cn.jzy.mapper.TbGoodsDescMapper;
import cn.jzy.mapper.TbGoodsMapper;
import cn.jzy.mapper.TbItemCatMapper;
import cn.jzy.mapper.TbItemMapper;
import cn.jzy.page.service.ItemPageService;
import cn.jzy.pojo.TbGoods;
import cn.jzy.pojo.TbGoodsDesc;
import cn.jzy.pojo.TbItem;
import cn.jzy.pojo.TbItemExample;
import com.alibaba.dubbo.config.annotation.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.solr.common.util.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: 林俊豪
 * @Date: 2019/6/10
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemCatMapper tbItemCatMapper;
    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public boolean genItemHtml(Long goodsId) {
        Configuration configuration = freeMarkerConfig.getConfiguration();
        try {
            // 模板
            Template template = configuration.getTemplate("item.ftl");
            // 创建数据模型
            Map dataModel = new HashMap<>();
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            // 1. 商品主表数据
            dataModel.put("goods", goods);
            // 2. 商品扩展表数据
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc", goodsDesc);
            // 3. 读取商品分类
            String itemCat1 = tbItemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String itemCat2 = tbItemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String itemCat3 = tbItemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
            dataModel.put("itemCat1", itemCat1);
            dataModel.put("itemCat2", itemCat2);
            dataModel.put("itemCat3", itemCat3);

            // 4. 读取SKU列表
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            criteria.andStatusEqualTo("1");// 状态有效
            // 按是否默认字段进行降序排序,目的是返回的结果第一条为默认SKU
            example.setOrderByClause("is_default desc");
            List<TbItem> itemList = itemMapper.selectByExample(example);
            dataModel.put("itemList", itemList);

            // 输出对象
            Writer out = new FileWriter(pagedir + goodsId + ".html");
            // 输出
            template.process(dataModel, out);
            out.close();
            return  true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
