package cn.jzy.search.controller;

import cn.jzy.search.service.ItemSearchService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Auther: 林俊豪
 * @Date: 2019/4/15
 */
@RestController
@RequestMapping("/itemsearch")
public class ItemSearchController {

    @Reference
    private ItemSearchService itemSearchService;


    @RequestMapping("/search")
    public Map search(@RequestBody Map searchMap){
        Map search = itemSearchService.search(searchMap);
        System.out.println(search);
        return search;
    }
}
