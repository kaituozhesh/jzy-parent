package cn.jzy.manager.controller;

import cn.jzy.pojo.TbGoods;
import cn.jzy.pojo.TbItem;
import cn.jzy.pojogroup.Goods;
import cn.jzy.sellergoods.service.GoodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return goodsService.findPage(page, rows);
	}

	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}

	@Autowired
	private Destination queueSolrDeleteDestination;
    @Autowired
	private Destination topicPageDeleteDestination;
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(final Long [] ids){
		try {
			goodsService.delete(ids);

			// 从索引库中删除
            jmsTemplate.send(queueSolrDeleteDestination, session -> session.createObjectMessage(ids));

            // 删除每个服务器上的商品详情页
            jmsTemplate.send(topicPageDeleteDestination, session -> session.createObjectMessage(ids));
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}


	@Autowired
	private JmsTemplate jmsTemplate;
    @Autowired
	private Destination queueSolrDestination;
    @Autowired
    private Destination topicPageDestination;
    // false 修改审核状态
	@RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {
        try {
            goodsService.updateStatus(ids,status,false);
            // 审核通过
            if ("1".equals(status)) {
                // 导入到索引库
                // 得到需要导入的SKU列表
                List<TbItem> itemList = goodsService.findItemListByGoodsListAndStatus(ids, status);
                // 转换为JSON传输 导入solr
                final String jsonString = JSON.toJSONString(itemList);
                jmsTemplate.send(queueSolrDestination, session -> session.createTextMessage(jsonString));

                // 生成商品详细页
                for (final Long goodsId : ids){
                    jmsTemplate.send(topicPageDestination, session -> session.createTextMessage(goodsId + ""));
                }
            }
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "失败");
        }
    }
/*
    @Reference(timeout = 40000)
    private ItemPageService itemPageService;

    @RequestMapping("/genHtml")
    public void genHtml(Long goodsId){
        itemPageService.genItemHtml(goodsId);
    }*/
}
