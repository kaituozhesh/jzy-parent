package cn.jzy.task;

import cn.jzy.mapper.TbSeckillGoodsMapper;
import cn.jzy.pojo.TbSeckillGoods;
import cn.jzy.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class SeckillTask {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Scheduled(cron = "0/10 * * * * ?")
    public void refreshSeckillGoods(){
        System.out.println("执行了任务调度" + new Date());

        List goodsIds =  new ArrayList(redisTemplate.boundHashOps("seckillGoods").keys());

        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        // 审核通过的商品
        criteria.andStatusEqualTo("1");
        // 库存数 大于 0
        criteria.andStockCountGreaterThan(0);
        // 开始日期小于当前日期
        criteria.andStartTimeLessThanOrEqualTo(new Date());
        // 截至日期大于等于当前日期
        criteria.andEndTimeGreaterThanOrEqualTo(new Date());
        // 排除缓存中已经存在的商品ID集合
        if (goodsIds.size() > 0) {
            criteria.andIdNotIn(goodsIds);
        }
        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);
        // 将列表数据装入缓存
        for (TbSeckillGoods seckillGoods : seckillGoodsList){
            redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
            System.out.println("增量更新秒杀商品ID : " + seckillGoods.getId());
        }

    }

    @Scheduled(cron = "* * * * * ?")
    public void removeSeckillGoods(){
        // 查询出缓存中的数据，扫描每条记录，判断时间，如果当前时间超过了截至时间，移除此记录
        List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
        System.out.println("执行了清楚秒杀商品的任务");
        for (TbSeckillGoods seckillGoods : seckillGoodsList){
            if (seckillGoods.getEndTime().getTime() < System.currentTimeMillis()) {
                // 同步到数据库中
                seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
                // 清楚缓存
                redisTemplate.boundHashOps("seckillGoods").delete(seckillGoods.getId());
                System.out.println("秒杀商品 : " + seckillGoods.getId() + " 已过期");
            }
        }

    }
}
