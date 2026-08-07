package cn.qihangerp.open.shein.helper;

import cn.qihangerp.open.shein.helper.model.*;
import cn.qihangerp.open.shein.helper.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SheinApiHelper {

    private static final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build();
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static ImageUploadResponse uploadImage(String openKeyId,String secretKey,String apiUrl,File imageFile, Long imageType) throws IOException {
        String url = apiUrl + "/open-api/goods/upload-pic";
        
        // ?
        Headers headers = createHeaders(openKeyId,secretKey,url);
        
        // ?
        RequestBody requestBody = new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image_type", String.valueOf(imageType))
            .addFormDataPart("file", imageFile.getName(),
                RequestBody.create(imageFile, MediaType.parse("image/*")))
            .build();
        
        // 
        Request request = new Request.Builder()
            .url(url)
            .headers(headers)
            .post(requestBody)
            .build();
        
        // 
        log.info("Request URL: {}", url);
        log.info("Request Headers: {}", headers);
        
        // ?
        try (Response response = client.newCall(request).execute()) {
            // 
            log.info("Response Code: {}", response.code());
            log.info("Response Headers: {}", response.headers());
            
            String responseBody = response.body() != null ? response.body().string() : null;

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code() + ", body: " + responseBody);
            }
            
            return objectMapper.readValue(responseBody, ImageUploadResponse.class);
        }
    }
    
    public static ProductPublishResponse publishProduct(ProductPublishRequest request) throws IOException {
        String url = request.getUrl() + "/open-api/goods/product/publishOrEdit";
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
            
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code() + ", body: " + responseBody);
            }
            
            return objectMapper.readValue(responseBody, ProductPublishResponse.class);
        }
    }
    
    /**
     * 
     */
    public static CategoryResponse queryCategoryTree(String openKeyId,String secretKey,String apiUrl) throws IOException {
        String url = apiUrl + "/open-api/goods/query-category-tree";
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        String urlPath = "/open-api/goods/query-category-tree";
        
        // 
        String randomKey = generateRandomKey();
        String value = openKeyId + "&" + timestamp + "&" + urlPath;
        String key = secretKey + randomKey;
        
        // HMAC-SHA256
        String signature;
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(value.getBytes(StandardCharsets.UTF_8));
            String hexHash = byteArrayToHexString(bytes);
            String base64Value = Base64.getEncoder().encodeToString(hexHash.getBytes(StandardCharsets.UTF_8));
            signature = randomKey + base64Value;
        } catch (Exception e) {
            throw new IOException("Generate signature failed", e);
        }
        
        // 
        Request request = new Request.Builder()
            .url(url)
            .post(RequestBody.create(MediaType.parse("application/json"), "{}"))
            .addHeader("Content-Type", "application/json")
            .addHeader("language", "zh-cn")
            .addHeader("x-lt-openKeyId", openKeyId)
            .addHeader("x-lt-timestamp", timestamp)
            .addHeader("x-lt-signature", signature)
            .build();
            
        log.info("Request URL: {}", url);
        log.info("Request Headers: {}", request.headers());
        
        // ?
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            return objectMapper.readValue(responseBody, CategoryResponse.class);
        }
    }
    
    public static AttributeTemplateResponse queryAttributeTemplate(String openKeyId,String secretKey,String apiUrl,List<Long> productTypeIds) throws IOException {
        String url = apiUrl + "/open-api/goods/query-attribute-template";
        
        // ?
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("product_type_id_list", productTypeIds);
        
        // 
        Request request = new Request.Builder()
            .url(url)
            .post(RequestBody.create(MediaType.parse("application/json"), objectMapper.writeValueAsString(requestBody)))
            .headers(createHeaders(openKeyId,secretKey,url))
            .build();
        
        // ?
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            log.info("? {}", responseBody);
            return objectMapper.readValue(responseBody, AttributeTemplateResponse.class);
        }
    }
    
    /**
     * ?
     */
    public static DocumentStateResponse queryDocumentState(DocumentStateRequest request) throws IOException {
        String url = request.getUrl() + "/open-api/goods/query-document-state";
        
        // ?
        Headers headers = createHeaders(request.getAppKey(),request.getAppSecret(),url);
        
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
            
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code() + ", body: " + responseBody);
            }
            
            return objectMapper.readValue(responseBody, DocumentStateResponse.class);
        }
    }
    
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
            
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code() + ", body: " + responseBody);
            }
            
            return objectMapper.readValue(responseBody, ProductListResponse.class);
        }
    }

    /**
     * SKU
     *
     * @param request 
     * @return SKU
     * @throws IOException API
     */
    public static SkuDetailResponse querySkuDetail(SkuDetailRequest request) throws IOException {
        String url = request.getUrl() + "/open-api/openapi-business-backend/product/full-detail";

        // ?
        Headers headers = createHeaders(request.getAppKey(),request.getAppSecret(),url);

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

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code() + ", body: " + responseBody);
            }

            return objectMapper.readValue(responseBody, SkuDetailResponse.class);
        }
    }

    
    public static Headers createHeaders(String openKeyId,String secretKey,String url) {
        // 
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        
        // URLcom?
        String urlPath = url.substring(url.indexOf("/open-api"));
        
        // ?
        String randomKey = generateRandomKey();
        
        // 
        String signature = generateSignature(openKeyId,secretKey,timestamp, urlPath, randomKey);
        
        // 
        log.info("URL Path: {}", urlPath);
        log.info("Timestamp: {}", timestamp);
        log.info("Random Key: {}", randomKey);
        log.info("Signature: {}", signature);
        log.info("Value to sign: {}", openKeyId + "&" + timestamp + "&" + urlPath);
        log.info("Key for signing: {}", secretKey + randomKey);
        
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
        log.info("Final Headers:");
        headers.forEach(pair -> log.info("{}: {}", pair.getFirst(), pair.getSecond()));
        
        return headers;
    }
    
    public static String generateRandomKey() {
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
    
    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            String hex_digit = String.format("%02x", b & 0xff);
            hex.append(hex_digit);
        }
        return hex.toString().toLowerCase();
    }

//    /**
//     * 
//     *
//     * @param request 
//     * @return SKU
//     * @throws IOException API
//     */
//    public static OrderListResponse queryOrderList(OrderListRequest request) throws IOException {
//        String url = request.getUrl() + "/open-api/order/order-list";
//
//        // ?
//        Headers headers = createHeaders(request.getAppKey(),request.getAppSecret(),url);
//
//        // ?
//        String requestJson = objectMapper.writeValueAsString(request);
//        RequestBody requestBody = RequestBody.create(requestJson, MediaType.parse("application/json; charset=utf-8"));
//
//        // 
//        Request httpRequest = new Request.Builder()
//                .url(url)
//                .headers(headers)
//                .post(requestBody)
//                .build();
//
//        // ?
//        try (Response response = client.newCall(httpRequest).execute()) {
//            String responseBody = response.body() != null ? response.body().string() : null;
//
//            if (!response.isSuccessful()) {
//                throw new IOException("Unexpected response code: " + response.code() + ", body: " + responseBody);
//            }
//
//            return objectMapper.readValue(responseBody, OrderListResponse.class);
//        }
//    }
//
//    /**
//     * 
//     *
//     * @param request 
//     * @return SKU
//     * @throws IOException API
//     */
//    public static OrderDetailResponse queryOrderDetail(OrderDetailRequest request) throws IOException {
//        String url = request.getUrl() + "/open-api/order/order-detail";
//
//        // ?
//        Headers headers = createHeaders(request.getAppKey(),request.getAppSecret(),url);
//
//        // ?
//        String requestJson = objectMapper.writeValueAsString(request);
//        RequestBody requestBody = RequestBody.create(requestJson, MediaType.parse("application/json; charset=utf-8"));
//
//        // 
//        Request httpRequest = new Request.Builder()
//                .url(url)
//                .headers(headers)
//                .post(requestBody)
//                .build();
//
//        // ?
//        try (Response response = client.newCall(httpRequest).execute()) {
//            String responseBody = response.body() != null ? response.body().string() : null;
//
//            if (!response.isSuccessful()) {
//                throw new IOException("Unexpected response code: " + response.code() + ", body: " + responseBody);
//            }
//
//            return objectMapper.readValue(responseBody, OrderDetailResponse.class);
//        }
//    }

    /**
     * 
     *
     * @param request 
     * @return SKU
     * @throws IOException API
     */
//    public static UpdateGoodsInventoryResponse syncGoodsInventory(UpdateGoodsInventoryRequest request) throws IOException {
//        String url = request.getUrl() + "/open-api/goods/stock-update";
//
//        // ?
//        Headers headers = createHeaders(request.getAppKey(),request.getAppSecret(),url);
//
//        // ?
//        String requestJson = objectMapper.writeValueAsString(request);
//        RequestBody requestBody = RequestBody.create(requestJson, MediaType.parse("application/json; charset=utf-8"));
//
//        // 
//        Request httpRequest = new Request.Builder()
//                .url(url)
//                .headers(headers)
//                .post(requestBody)
//                .build();
//
//        // ?
//        try (Response response = client.newCall(httpRequest).execute()) {
//            String responseBody = response.body() != null ? response.body().string() : null;
//
//            if (!response.isSuccessful()) {
//                throw new IOException("Unexpected response code: " + response.code() + ", body: " + responseBody);
//            }
//
//            return objectMapper.readValue(responseBody, UpdateGoodsInventoryResponse.class);
//        }
//    }
} 