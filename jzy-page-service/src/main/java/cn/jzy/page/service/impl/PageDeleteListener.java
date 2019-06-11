package cn.jzy.page.service.impl;

import cn.jzy.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;

/**
 * @Auther: 林俊豪
 * @Date: 2019/6/11
 */
@Component
public class PageDeleteListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            Long [] goodsIds = (Long[]) objectMessage.getObject();
            System.out.println("删除页面: " + goodsIds);
            itemPageService.deleteItemHtml(goodsIds);
            System.out.println("页面删除成功");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
