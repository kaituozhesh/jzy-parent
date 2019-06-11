package cn.jzy.search.service.impl;

import cn.jzy.pojo.TbItem;
import cn.jzy.search.service.ItemSearchService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

/**
 * @Auther: 林俊豪
 * @Date: 2019/6/11
 */
@Component
public class ItemSearchListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {

        TextMessage textMessage = (TextMessage) message;
        try {
            // json 字符串
            String text = textMessage.getText();
            System.out.println("监听到消息 : " + text);
            List<TbItem> itemList = JSON.parseArray(text, TbItem.class);
            itemSearchService.importList(itemList);
            System.out.println("导入到solr索引库");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
