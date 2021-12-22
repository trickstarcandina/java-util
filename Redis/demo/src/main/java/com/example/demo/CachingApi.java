package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Synchronized;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class CachingApi {
    private ObjectMapper objectMapper;

    private String REDIS_HOST;
    private int REDIS_PORT;
    private String REDIS_PASS;

    Jedis jedis;

    public CachingApi() {
        objectMapper = new ObjectMapper();
    }

    public boolean initJedis() {
        try {
            jedis = new Jedis(REDIS_HOST, REDIS_PORT);
            jedis.auth(REDIS_PASS);
            jedis.select(0);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Synchronized
    public Object cachingGetTotal(String cusId, String dateFrom, String dateTo) {
        if (jedis == null && !initJedis()) return null;
        if (cusId == null || dateFrom == null || dateTo == null) {
            return null;
        }
        try {
            String data = jedis.get(cusId + "_" + dateFrom + "_" + dateTo);
            if (data == null) return null;
            UserCachingModel userCachingModel = objectMapper.readValue(data, UserCachingModel.class);
            return userCachingModel.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Synchronized
    public boolean cachingSetTotal(String cusId, String dateFrom, String dateTo, Object totalMoneyResponse) {
        if (jedis == null && !initJedis()) return false;
        if (cusId == null || dateFrom == null || dateTo == null) {
            return false;
        }
        UserCachingModel cache = new UserCachingModel();
        cache.setFrom(dateFrom);
        cache.setCusId(cusId);
        cache.setTo(dateTo);
        cache.setData(totalMoneyResponse);
        cache.setExp(System.currentTimeMillis() + 10 * 60 * 1000);

        try {
            jedis.setex(cusId + "_" + dateFrom + "_" + dateTo, 10 * 60, objectMapper.writeValueAsString(cache));
            return true;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return false;
    }
}
