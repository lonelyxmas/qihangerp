package cn.qihangerp.open.shein.helper;

import cn.qihangerp.open.shein.helper.model.*;
import cn.qihangerp.open.shein.helper.model.CategoryResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SheinShopApiHelper {

    private static final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build();
    
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public static CategoryResponse queryCategoryTree(String serverUrl, String appKey, String appSecret) throws IOException {
        String url = serverUrl + "/open-api/goods/query-category-tree";
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        String urlPath = "/open-api/goods/query-category-tree";

        // 
        String randomKey = SheinApiHelper.generateRandomKey();
        String value = appKey + "&" + timestamp + "&" + urlPath;
        String key = appSecret + randomKey;

        // HMAC-SHA256
        String signature;
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(value.getBytes(StandardCharsets.UTF_8));
            String hexHash = SheinApiHelper.byteArrayToHexString(bytes);
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
                .addHeader("x-lt-openKeyId", appKey)
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



} 