package cn.qihangerp.open.shein.helper;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
@Slf4j
public class SheinSignatureHelper {

    public static Headers createHeaders(String openKeyId, String secretKey, String url) {
        // 获取时间戳（毫秒级）
        String timestamp = String.valueOf(Instant.now().toEpochMilli());

        // 获取URL路径（从域名com后开始截取到结尾）
        String urlPath = url.substring(url.indexOf("/open-api"));

        // 生成随机码
        String randomKey = generateRandomKey();

        // 构建签名
        String signature = generateSignature(openKeyId,secretKey,timestamp, urlPath, randomKey);

        // 打印签名相关信息
        log.info("URL Path: {}", urlPath);
        log.info("Timestamp: {}", timestamp);
        log.info("Random Key: {}", randomKey);
        log.info("Signature: {}", signature);
        log.info("Value to sign: {}", openKeyId + "&" + timestamp + "&" + urlPath);
        log.info("Key for signing: {}", secretKey + randomKey);

        // 构建请求头
        Headers.Builder builder = new Headers.Builder()
                .add("Content-Type", "multipart/form-data")
//            .add("Content-Type", "application/json")
                .add("Accept", "application/json")
                .add("Accept-Charset", "UTF-8")
                .add("language", "zh-cn")
                .add("x-lt-openKeyId", openKeyId)
                .add("x-lt-timestamp", timestamp)
                .add("x-lt-signature", signature);

        // 打印最终请求头
        Headers headers = builder.build();
        log.info("Final Headers:");
        headers.forEach(pair -> log.info("{}: {}", pair.getFirst(), pair.getSecond()));

        return headers;
    }
    public static Headers createHeadersJson(String openKeyId,String secretKey,String url) {
        // 获取时间戳（毫秒级）
        String timestamp = String.valueOf(Instant.now().toEpochMilli());

        // 获取URL路径（从域名com后开始截取到结尾）
        String urlPath = url.substring(url.indexOf("/open-api"));

        // 生成随机码
        String randomKey = generateRandomKey();

        // 构建签名
        String signature = generateSignature(openKeyId,secretKey,timestamp, urlPath, randomKey);

        // 打印签名相关信息
        log.info("URL Path: {}", urlPath);
        log.info("Timestamp: {}", timestamp);
        log.info("Random Key: {}", randomKey);
        log.info("Signature: {}", signature);
        log.info("Value to sign: {}", openKeyId + "&" + timestamp + "&" + urlPath);
        log.info("Key for signing: {}", secretKey + randomKey);

        // 构建请求头
        Headers.Builder builder = new Headers.Builder()

                .add("Content-Type", "application/json")
//                .add("Accept", "application/json")
//                .add("Accept-Charset", "UTF-8")
//                .add("language", "zh-cn")
                .add("x-lt-openKeyId", openKeyId)
                .add("x-lt-timestamp", timestamp)
                .add("x-lt-signature", signature);

        // 打印最终请求头
        Headers headers = builder.build();
        log.info("Final Headers:");
        headers.forEach(pair -> log.info("{}: {}", pair.getFirst(), pair.getSecond()));

        return headers;
    }

    private static String generateRandomKey() {
        return String.format("%05d", (int)(Math.random() * 100000));
    }

    private static String generateSignature(String openKeyId,String secretKey,String timestamp, String urlPath, String randomKey) {
        try {
            // 构建签名字符串：openKeyId + "&" + timestamp + "&" + urlPath
            String value = openKeyId + "&" + timestamp + "&" + urlPath;

            // 构建密钥：secretKey + randomKey
            String key = secretKey + randomKey;

            // 使用HMAC-SHA256计算签名
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            // 计算签名
            byte[] bytes = sha256_HMAC.doFinal(value.getBytes(StandardCharsets.UTF_8));

            // 转换为十六进制字符串
            String hexHash = byteArrayToHexString(bytes);

            // 对十六进制字符串进行Base64编码
            String base64Value = Base64.getEncoder().encodeToString(hexHash.getBytes(StandardCharsets.UTF_8));

            // 将randomKey拼接在签名前面
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
