package cn.jzy.solrutil;

import cn.jzy.mapper.TbItemMapper;
import cn.jzy.pojo.TbItem;
import cn.jzy.pojo.TbItemExample;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Auther: 林俊豪
 * @Date: 2019/6/9
 */
@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    public void importItemData(){
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//审核通过的才导入
        List<TbItem> itemList = itemMapper.selectByExample(example);
        System.out.println("商品列表");
        for (TbItem item : itemList){
            System.out.println(item.getTitle());
            // 从数据库中提取规格的json字符串转换为map
            item.setSpecMap(JSON.parseObject(item.getSpec(), Map.class));
        }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
        System.out.println("结束");
    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
        solrUtil.importItemData();
    }
}
