package com.bookmall.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;

public class EcpayUtils {
	
	public static String generateCheckMacValue(Map<String, String> params, String hashKey, String hashIV) {
        // 1. 參數排序 (A-Z)
        TreeMap<String, String> sortedParams = new TreeMap<>(params);
        
        // 2. 拼接原始字串
        StringBuilder sb = new StringBuilder();
        sb.append("HashKey=").append(hashKey);
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        sb.append("&HashIV=").append(hashIV);

        // 3. URL 編碼與特殊符號處理
        String encoded = ecpayUrlEncode(sb.toString());
        
        // 4. 全轉小寫 (綠界規範)
        encoded = encoded.toLowerCase();

        System.out.println("Final String to Hash: " + encoded);

        // 5. 使用 DigestUtils 進行 SHA-256 雜湊，並轉大寫
        return DigestUtils.sha256Hex(encoded).toUpperCase();
    }

    /**
     * 遵循綠界 AIO V5 規範的編碼轉換
     */
    private static String ecpayUrlEncode(String str) {
        try {
            // 先進行標準編碼
            String encoded = URLEncoder.encode(str, StandardCharsets.UTF_8.toString());

            // 僅還原綠界指定的 7 種符號
            // 注意：不還原 /、:、=、&
            return encoded.replace("%2D", "-").replace("%2d", "-")
                          .replace("%5F", "_").replace("%5f", "_")
                          .replace("%2E", ".").replace("%2e", ".")
                          .replace("%21", "!").replace("%21", "!")
                          .replace("%2A", "*").replace("%2a", "*")
                          .replace("%28", "(").replace("%28", "(")
                          .replace("%29", ")").replace("%29", ")");
        } catch (Exception e) {
            throw new RuntimeException("Encoding failed", e);
        }
    }

//	public static String generateCheckMacValue(Map<String, String> params, String hashKey, String hashIV) {
//        // 1. 排序：TreeMap 會處理 A-Z 排序
//        TreeMap<String, String> sortedParams = new TreeMap<>(params);
//        
//        // 2. 拼接原始字串
//        StringBuilder sb = new StringBuilder();
//        sb.append("HashKey=").append(hashKey);
//        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
//            sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
//        }
//        sb.append("&HashIV=").append(hashIV);
//
//        // 3. 進行 URL Encode
//        String encoded = ecpayUrlEncode(sb.toString());
//        
//        // 4. 關鍵：整串轉小寫
//        encoded = encoded.toLowerCase();
//
//        System.out.println("Final String to Hash: " + encoded);
//
//        // 5. SHA256 加密並轉大寫
//        return sha256(encoded).toUpperCase();
//    }
//
//    private static String ecpayUrlEncode(String str) {
//        try {
//            String encoded = URLEncoder.encode(str, StandardCharsets.UTF_8.toString());
//            // 綠界 AIO V5 嚴格取代規則：將這些符號換回原始字元
//            return encoded.replace("+", "%20")
//                          .replace("%2D", "-").replace("%2d", "-")
//                          .replace("%5F", "_").replace("%5f", "_")
//                          .replace("%2E", ".").replace("%2e", ".")
//                          .replace("%21", "!").replace("%21", "!")
//                          .replace("%2A", "*").replace("%2a", "*")
//                          .replace("%28", "(").replace("%28", "(")
//                          .replace("%29", ")").replace("%29", ")");
//        } catch (Exception e) {
//            throw new RuntimeException("Encoding failed", e);
//        }
//    }

    private static String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}