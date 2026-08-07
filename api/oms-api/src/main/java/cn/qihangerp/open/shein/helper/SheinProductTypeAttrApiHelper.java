package cn.qihangerp.open.shein.helper;

import cn.qihangerp.open.shein.helper.model.AttributeTemplateResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SheinProductTypeAttrApiHelper {

    private static final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build();
    
    private static final ObjectMapper objectMapper = new ObjectMapper();


    public static AttributeTemplateResponse queryAttributeTemplate(String serverUrl,String appKey,String appSecret,List<Long> productTypeIds) throws IOException {
        String url = serverUrl + "/open-api/goods/query-attribute-template";

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("product_type_id_list", productTypeIds);

        // 构建请求
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), objectMapper.writeValueAsString(requestBody)))
                .headers(SheinApiHelper.createHeaders(appKey,appSecret,url))
                .build();

        // 发送请求
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
//            log.info("属性模板响应: {}", responseBody);
            return objectMapper.readValue(responseBody, AttributeTemplateResponse.class);
        }
    }

} 