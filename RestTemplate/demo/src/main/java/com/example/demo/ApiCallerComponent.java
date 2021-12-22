package com.example.demo;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.glassfish.jersey.media.multipart.MultiPart;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Component
public class ApiCallerComponent {

    private Map<String, Boolean> mapExtFile = new HashMap<>();

    //init
    public ApiCallerComponent() {
        mapExtFile.put(".jpg", true);
        mapExtFile.put(".mp3", true);
        mapExtFile.put(".jpeg", true);
        mapExtFile.put(".png", true);
        mapExtFile.put(".mp4", true);
        mapExtFile.put(".mpa", true);
        mapExtFile.put(".aif", true);
        mapExtFile.put(".mid", true);
        mapExtFile.put(".wma", true);
        mapExtFile.put(".csv", true);
        mapExtFile.put(".gif", true);
        mapExtFile.put(".ico", true);
        mapExtFile.put(".psd", true);
        mapExtFile.put(".tif", true);
        mapExtFile.put(".avi", true);
        mapExtFile.put(".heif", true);
        mapExtFile.put(".heic", true);
    }

    public RestTemplate getRestTemplate(int time) {
        CloseableHttpClient httpClient
                = HttpClients.custom()
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);
        requestFactory.setReadTimeout(time);
        requestFactory.setConnectTimeout(time);
        requestFactory.setConnectionRequestTimeout(time);
        return new RestTemplate(requestFactory);
    }

    public Client getClient(int time) {
        ClientConfig configuration30 = new ClientConfig();
        configuration30.property(ClientProperties.CONNECT_TIMEOUT, time);
        configuration30.property(ClientProperties.READ_TIMEOUT, time);
        return ClientBuilder.newClient(configuration30);
    }

    //post
    public <T, E> E post(String token, String url, T jsonObject, Class<E> typeParameterClass, int timeout) {
        //add header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Content-Type", "application/json;charset=UTF-8");
        if (token != null) {
            headers.set("Authorization", token);
        }

        HttpEntity<T> entity = new HttpEntity<>(jsonObject, headers);
        try {
            ResponseEntity<E> responseEntity = getRestTemplate(timeout).exchange(url, HttpMethod.POST, entity, typeParameterClass);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return responseEntity.getBody();
            }
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T, E> CompletableFuture<E> postDataForm(String token, String url, T jsonObject, Class<E> typeParameterClass, int timeout) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Content-Type", "application/json;charset=UTF-8");
        if (token != null) {
            headers.set("Token", token);
        }

        HttpEntity<T> entity = new HttpEntity<>(jsonObject, headers);
        try {
            E response = getRestTemplate(timeout).postForObject(url, jsonObject, typeParameterClass, entity);
            return CompletableFuture.completedFuture(response);

        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResponseEntity<BaseResponse> postDataForm2(String token, String url, int timeout, Map<String, Object> map) {
        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.add("Authorization", "Bearer " + token);
        }
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<BaseResponse> response = getRestTemplate(timeout).postForEntity(url, entity , BaseResponse.class );
        return response;
    }

    //get
    public <T, E> E get(String token, String url, Class<E> typeParameterClass, int timeout) {
        //add header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Content-Type", "application/json;charset=UTF-8");
        if (token != null) {
            headers.set("Token", token);
        }

        HttpEntity<T> entity = new HttpEntity<>(null, headers);
        try {
            ResponseEntity<E> responseEntity = getRestTemplate(timeout).exchange(url, HttpMethod.GET, entity, typeParameterClass);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return responseEntity.getBody();
            }
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T, E> E getDataForm(String token, String url, T jsonObject, Class<E> typeParameterClass, int timeout) {
        //add header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Content-Type", "application/json;charset=UTF-8");
        if (token != null) {
            headers.set("Token", token);
        }

        HttpEntity<T> entity = new HttpEntity<>(jsonObject, headers);
        try {
            ResponseEntity<E> responseEntity = getRestTemplate(timeout).exchange(url, HttpMethod.GET, entity, typeParameterClass);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return responseEntity.getBody();
            }
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T, E> E put(String token, String url, Class<E> typeParameterClass, int timeout) {
        //add header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Content-Type", "application/json;charset=UTF-8");
        if (token != null) {
            headers.set("Token", token);
        }

        HttpEntity<T> entity = new HttpEntity<>(null, headers);
        try {
            ResponseEntity<E> responseEntity = getRestTemplate(timeout).exchange(url, HttpMethod.PUT, entity, typeParameterClass);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return responseEntity.getBody();
            }
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T, E> E delete(String token, String url, Class<E> typeParameterClass, int timeout) {
        //add header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Content-Type", "application/json;charset=UTF-8");
        if (token != null) {
            headers.set("Token", token);
        }

        HttpEntity<T> entity = new HttpEntity<>(null, headers);
        try {
            ResponseEntity<E> responseEntity = getRestTemplate(timeout).exchange(url, HttpMethod.DELETE, entity, typeParameterClass);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return responseEntity.getBody();
            }
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    //file
    public Object getResult(String token, String encodedURL, String customer_id, String brand_name, String description,
                                       MultipartFile[] website_image, MultipartFile[] service_contract, MultipartFile[] certificate_of_business_registration,
                                       MultipartFile[] letter_of_authorization, MultipartFile[] registration_official_dispatch, String is_authorization, int timeout) {
        try (MultiPart multiPart = new FormDataMultiPart().field("customer_id", customer_id, javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE)
                .field("brand_name", brand_name, javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE)
                .field("description", description, javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE)
                .field("is_authorization", is_authorization, javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE)) {
            BaseResponse baseResponse = addPart(multiPart, website_image, "website_image");
            if (baseResponse == null || baseResponse.getError()) return baseResponse;
            baseResponse = addPart(multiPart, service_contract, "service_contract");
            if (baseResponse == null || baseResponse.getError()) return baseResponse;
            baseResponse = addPart(multiPart, certificate_of_business_registration, "certificate_of_business_registration");
            if (baseResponse == null || baseResponse.getError()) return baseResponse;
            baseResponse = addPart(multiPart, letter_of_authorization, "letter_of_authorization");
            if (baseResponse == null || baseResponse.getError()) return baseResponse;
            baseResponse = addPart(multiPart, registration_official_dispatch, "registration_official_dispatch");
            if (baseResponse == null || baseResponse.getError()) return baseResponse;
            return getClient(timeout).target(encodedURL).request().header("Token", token).post(Entity.entity(multiPart, multiPart.getMediaType()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BaseResponse addPart(MultiPart multiPart, MultipartFile[] files, String name) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(201);
        baseResponse.setError(true);
        List<Path> listPath = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            try {
                if (files[i].isEmpty()) continue;
                int index = files[i].getOriginalFilename().lastIndexOf(".");
                if (index == -1) return null;
                if (!mapExtFile.containsKey(files[i].getOriginalFilename().substring(index + 1))) {
                    baseResponse.setMessage("Đuôi file không đúng!");
                    return baseResponse;
                }
                Path tempFile = Files.createTempFile(null, files[i].getOriginalFilename().substring(index));
                listPath.add(tempFile);
                Files.write(tempFile, files[i].getBytes());
                File fileToSend = tempFile.toFile();
                FileDataBodyPart fileDataBodyPart = new FileDataBodyPart(name,
                        fileToSend,
                        javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM_TYPE);
                multiPart.bodyPart(fileDataBodyPart);
            } catch (Exception e) {
            }
        }
        baseResponse.setStatus(200);
        baseResponse.setError(false);
        return baseResponse;
    }

    public Object saveMultiImage(String token, String encodedURL, MultipartFile[] files, String form, int timeout) {
        try (MultiPart multiPart = new FormDataMultiPart().field("form", form, javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE)) {
            BaseResponse baseResponse = addPart(multiPart, files, "files");
            if (baseResponse == null || baseResponse.getError()) return baseResponse;
            return getClient(timeout).target(encodedURL).request().header("Token", token).post(Entity.entity(multiPart, multiPart.getMediaType()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //kafka
}
