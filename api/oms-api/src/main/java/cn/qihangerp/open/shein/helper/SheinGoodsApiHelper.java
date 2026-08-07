package cn.qihangerp.open.shein.helper;

import cn.qihangerp.open.shein.helper.model.*;
import cn.qihangerp.open.shein.helper.model.ProductListRequest;
import cn.qihangerp.open.shein.helper.model.ProductListResponse;
import cn.qihangerp.open.shein.helper.response.SheinProductDetailResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SheinGoodsApiHelper {

    private static final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build();
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 
     */
    public static ProductListResponse queryProductList(ProductListRequest request) throws IOException {
        String url = request.getUrl() + "/open-api/openapi-business-backend/product/query";
        String appKey = request.getAppKey();
        String appSecret = request.getAppSecret();
        // ?
        Headers headers = createHeaders(appKey,appSecret,url);
        
        // ?
        String requestJson = objectMapper.writeValueAsString(request);
        RequestBody requestBody = RequestBody.create(requestJson, MediaType.parse("application/json; charset=utf-8"));
        
        // 
        Request httpRequest = new Request.Builder()
            .url(url)
            .headers(headers)
            .post(requestBody)
            .build();
        
        // ?
        try (Response response = client.newCall(httpRequest).execute()) {
            String responseBody = response.body() != null ? response.body().string() : null;
            log.info("====responseBody======={}",responseBody);
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code() + ", body: " + responseBody);
            }
            
            return objectMapper.readValue(responseBody, ProductListResponse.class);
        }
    }

    /**
     * SKU
     *
     * @param
     * @return SKU
     * @throws IOException API
     */
    public static SheinProductDetailResponse queryProductDetail(String spuName, String serverUrl, String appKey, String secret) throws IOException {
        String url = serverUrl+ "/open-api/goods/spu-info";

        // ?
        Headers headers = createHeaders(appKey,secret,url);

        // ?
        Map<String,Object> params = new HashMap<>();
        params.put("spuName",spuName);
        params.put("languageList",new String[]{"zh-cn","en"});
        String requestJson = objectMapper.writeValueAsString(params);
        RequestBody requestBody = RequestBody.create(requestJson, MediaType.parse("application/json; charset=utf-8"));

        // 
        Request httpRequest = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(requestBody)
                .build();

        // ?
        try (Response response = client.newCall(httpRequest).execute()) {
            String responseBody = response.body() != null ? response.body().string() : null;

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code() + ", body: " + responseBody);
            }

            return objectMapper.readValue(responseBody, SheinProductDetailResponse.class);
        }
    }

    
    private static Headers createHeaders(String openKeyId,String secretKey,String url) {
        // 
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        
        // URLcom?
        String urlPath = url.substring(url.indexOf("/open-api"));
        
        // ?
        String randomKey = generateRandomKey();
        
        // 
        String signature = generateSignature(openKeyId,secretKey,timestamp, urlPath, randomKey);
        
        // 
//        log.info("URL Path: {}", urlPath);
//        log.info("Timestamp: {}", timestamp);
//        log.info("Random Key: {}", randomKey);
//        log.info("Signature: {}", signature);
//        log.info("Value to sign: {}", openKeyId + "&" + timestamp + "&" + urlPath);
//        log.info("Key for signing: {}", secretKey + randomKey);
        
        // ?
        Headers.Builder builder = new Headers.Builder()
            .add("Content-Type", "multipart/form-data")
            .add("Accept", "application/json")
            .add("Accept-Charset", "UTF-8")
            .add("language", "zh-cn")
            .add("x-lt-openKeyId", openKeyId)
            .add("x-lt-timestamp", timestamp)
            .add("x-lt-signature", signature);
            
        // 
        Headers headers = builder.build();
//        log.info("Final Headers:");
//        headers.forEach(pair -> log.info("{}: {}", pair.getFirst(), pair.getSecond()));
        
        return headers;
    }
    
    private static String generateRandomKey() {
        return String.format("%05d", (int)(Math.random() * 100000));
    }
    
    private static String generateSignature(String openKeyId,String secretKey,String timestamp, String urlPath, String randomKey) {
        try {
            // openKeyId + "&" + timestamp + "&" + urlPath
            String value = openKeyId + "&" + timestamp + "&" + urlPath;
            
            // secretKey + randomKey
            String key = secretKey + randomKey;
            
            // HMAC-SHA256
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            
            // 
            byte[] bytes = sha256_HMAC.doFinal(value.getBytes(StandardCharsets.UTF_8));
            
            // 
            String hexHash = byteArrayToHexString(bytes);
            
            // Base64
            String base64Value = Base64.getEncoder().encodeToString(hexHash.getBytes(StandardCharsets.UTF_8));
            
            // randomKey?
            return randomKey + base64Value;
            
        } catch (Exception e) {
            log.error("Generate signature failed", e);
            throw new RuntimeException("Generate signature failed", e);
        }
    }
    
    private static String byteArrayToHexString(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            String hex_digit = String.format("%02x", b & 0xff);
            hex.append(hex_digit);
        }
        return hex.toString().toLowerCase();
    }




} 