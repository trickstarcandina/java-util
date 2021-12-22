package com.example.demo;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
public class DemoController {

    @Autowired
    private CachingApi cachingApi;

    @Autowired
    private DemoService service;

    @RequestMapping(value = "/{name}", method = RequestMethod.POST)
    public ResponseEntity forwardRequest(HttpServletRequest req, @PathVariable("name") String pathName, @RequestBody Object form) {
        try {
            String dataRequest = new Gson().toJson(form);
            JsonElement element = new Gson().fromJson(dataRequest, JsonElement.class);
            JsonObject jsonObject = element.getAsJsonObject();
            if (jsonObject != null && jsonObject.has("id")) {
                jsonObject.remove("id");
                jsonObject.addProperty("id", "id");
            }
            // get data from cache redis
            Object cache = cachingApi.cachingGetTotal(jsonObject.get("id").toString(),
                    jsonObject.get("fromDate").toString(),
                    jsonObject.get("toDate").toString());
            if (cache != null) {
                return ResponseEntity.ok(cache);
            }
            Object dataResponse = service.forwardRequest("id", pathName, jsonObject);
            // save data into cache redis
            if (dataResponse != null) {
                cachingApi.cachingSetTotal(jsonObject.get("id").toString(),
                        jsonObject.get("fromDate").toString(),
                        jsonObject.get("toDate").toString(),
                        dataResponse);
            }
            return ResponseEntity.ok(dataResponse);
        } catch (Exception e) {
        }
        return null;
    }
}
