package com.example.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApiResponse {

    @Autowired
    ApiCallerComponent apiCallerComponent;


    //extra response
    public Response apiResponse(String url, String authorization) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        HttpEntity httpEntity = new HttpEntity<>(headers);
        try {
//            apiCallerComponent ?? có thể dùng hoặc không
            ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
            ObjectMapper mapper = new ObjectMapper();
            List<DataDemo> dataResponse = mapper.convertValue(responseEntity.getBody().get("data"), new TypeReference<List<DataDemo>>(){});
            for(int i = 0; i< dataResponse.size(); i++) {
//                ...
                }
            //return something
            return null;
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    //second way
    //https://stackoverflow.com/questions/2591098/how-to-parse-json-in-java
//    {
//        "pageInfo": {
//        "pageName": "abc",
//                "pagePic": "http://example.com/content.jpg"
//    },
//        "posts": [
//        {
//            "post_id": "123456789012_123456789012",
//                "actor_id": "1234567890",
//                "picOfPersonWhoPosted": "http://example.com/photo.jpg",
//                "nameOfPersonWhoPosted": "Jane Doe",
//                "message": "Sounds cool. Can't wait to see it!",
//                "likesCount": "2",
//                "comments": [],
//            "timeOfPost": "1234567890"
//        }
//        ]
//    }
//    The org.json library is easy to use.
//    Just remember (while casting or using methods like getJSONObject and getJSONArray) that in JSON notation
//
//    [ … ] represents an array, so library will parse it to JSONArray
//    { … } represents an object, so library will parse it to JSONObject
//    Example code below:

//    import org.json.*;

//    String jsonString = ... ; //assign your JSON String here
//    JSONObject obj = new JSONObject(jsonString);
//    String pageName = obj.getJSONObject("pageInfo").getString("pageName");
//
//    JSONArray arr = obj.getJSONArray("posts"); // notice that `"posts": [...]`
//    for (int i = 0; i < arr.length(); i++)
//    {
//        String post_id = arr.getJSONObject(i).getString("post_id");
//    ......
//    }
}
