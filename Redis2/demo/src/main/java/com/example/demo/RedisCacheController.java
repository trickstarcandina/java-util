package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/redis")
public class RedisCacheController {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RedisCacheDAO redisCacheDAO;

    @Autowired
    DemoService demoService;

    @RequestMapping(value = "commodityStatistics/add", method = RequestMethod.POST)
    public ResponseEntity saveCommodityStatistics(HttpServletRequest req, @RequestParam("id") Long id) {
        List<CommodityStatistics> commodityStatistics = demoService.commodityStatistics(id);
        try{
            for(int i=0; i< commodityStatistics.size(); i++){
                redisCacheDAO.saveCommodityStatistics(new CommodityStatistics(UUID.randomUUID().toString(), id, commodityStatistics.get(i).getName(), commodityStatistics.get(i).getValue()));
            }
//        redisCacheDAO.save(new CommodityStatistics(token.getUserId(), commodityStatistics));
        } catch (Exception e){
            return ResponseEntity.ok().body("Update redis fail");
        }
        return ResponseEntity.ok().body("OK");
    }

    @RequestMapping(value = "commodityStatistics/findAll", method = RequestMethod.GET)
    @Transactional(timeout = 120)
    public ResponseEntity findAllCommodityStatistics(HttpServletRequest req, @RequestParam("id") Long id) {
        try{
            //chưa tồn tại
            if (!redisCacheDAO.checkExistCommodityStatistics()){
                //load to redis
                List<CommodityStatistics> commodityStatistics = demoService.commodityStatistics(id);
                for(int i=0; i< commodityStatistics.size(); i++){
                    redisCacheDAO.saveCommodityStatistics(new CommodityStatistics(UUID.randomUUID().toString(), id, commodityStatistics.get(i).getName(), commodityStatistics.get(i).getValue()));
                }
                return ResponseEntity.ok().body(commodityStatistics);
            }
            //nếu đã tồn tại thì get
            List<CommodityStatistics> commodityStatisticsList = redisCacheDAO.findAllCommodityStatistics(id);
            if(!commodityStatisticsList.isEmpty() && commodityStatisticsList != null){
                return ResponseEntity.ok().body(commodityStatisticsList);
            }
        } catch (Exception e){
        }
        return ResponseEntity.ok().body(demoService.commodityStatistics(id));
    }

}

