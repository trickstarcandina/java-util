package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisCacheDAO {

    public static final String KEY_COMMODITY = "CommodityStatistics";

    @Autowired
    private RedisTemplate template;

    public boolean checkExistCommodityStatistics(){
        return template.hasKey(KEY_COMMODITY) ? true : false;
    }

    public void saveCommodityStatistics(CommodityStatistics commodityStatistics){
        template.expire(KEY_COMMODITY, 30, TimeUnit.SECONDS);
        template.opsForHash().put(KEY_COMMODITY, commodityStatistics.getUuID() ,commodityStatistics);
    }

    public List<CommodityStatistics> findAllCommodityStatistics(Long cusId){
        List<CommodityStatistics> commodityStatisticsList = template.opsForHash().values(KEY_COMMODITY);
        List<CommodityStatistics> commodityStatistics = new ArrayList<>();
        for (int i=0; i<commodityStatisticsList.size(); i++){
            if(commodityStatisticsList.get(i).getCusId().equals(cusId)){
                commodityStatistics.add(commodityStatisticsList.get(i));
            }
        }
        return commodityStatistics;
    }

}

