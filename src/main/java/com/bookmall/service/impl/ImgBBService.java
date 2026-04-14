package com.bookmall.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImgBBService {

    @Value("${imgbb.api.key}")
    private String apiKey;

    public String uploadImage(MultipartFile file) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.imgbb.com/1/upload?key=" + apiKey;

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", file.getResource());

        // 直接將 JSON 映射到我們定義的 ImgBBResponse 類別
        ImgBBResponse response = restTemplate.postForObject(url, body, ImgBBResponse.class);

        if (response != null && response.success) {
            return response.data.url; // 這裡就跟 JavaScript 一樣簡單了！
        }
        throw new RuntimeException("圖片上傳失敗");
    }

    // --- 定義與 API 手冊結構完全一致的靜態內部類別 ---
    private static class ImgBBResponse {
        public ImgBBData data;   // 對應 JSON 的 "data"
        public boolean success;  // 對應 JSON 的 "success"
        public int status;       // 對應 JSON 的 "status"
    }

    private static class ImgBBData {
        public String url;       // 對應 JSON 的 "url"
    }
}