package cn.jzy.search.service.impl;

import cn.jzy.pojo.TbItem;
import cn.jzy.search.service.ItemSearchService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: 林俊豪
 * @Date: 2019/6/9
 */
@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map search(Map searchMap) {
        Map map = new HashMap();

        // 查询列表
        map.putAll(searchList(searchMap));

        // 2. 分组查询商品分类列表
        List<String> categoryList = searchCatgoryList(searchMap);
        map.put("categoryList",categoryList);
        return map;
    }

    // 查询列表
    private Map searchList(Map searchMap){
        Map map = new HashMap();

        // 高亮显示
        HighlightQuery query = new SimpleHighlightQuery();
        // 高亮选项
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        highlightOptions.setSimplePrefix("<em style = 'color:red'>");
        highlightOptions.setSimplePostfix("</em>");

        // 为查询对象设置高亮选项
        query.setHighlightOptions(highlightOptions);

        // 关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        // 高亮页对象
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        // 高亮入口集合
        List<HighlightEntry<TbItem>> entryList = page.getHighlighted();
        for (HighlightEntry<TbItem> entry : entryList){
            // 获取高亮列表
            List<HighlightEntry.Highlight> highlightList = entry.getHighlights();
            /*for (HighlightEntry.Highlight h : highlightList){
                List<String> sns = h.getSnipplets();
                System.out.println(sns);
            }*/
            if (highlightList.size() > 0 && highlightList.get(0).getSnipplets().size() > 0){
                TbItem item = entry.getEntity();
                item.setTitle(highlightList.get(0).getSnipplets().get(0));
            }
        }
        map.put("rows", page.getContent());
        return map;
    }
    // 分组查询 ( 查询商品分类列表 )
    private List<String> searchCatgoryList(Map searchMap){
        List<String> list = new ArrayList();
        Query query = new SimpleQuery("*:*");
        // 关键字查询  where
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        // 设置分组选项 group by
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        // 获取分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        // 获取分组结果对象
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        // 获取分组入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        // 获取分组入口集合
        List<GroupEntry<TbItem>> entryList = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : entryList){
            list.add(entry.getGroupValue());
        }
        return list; // 将分组的结果添加到返回值中
    }
}
