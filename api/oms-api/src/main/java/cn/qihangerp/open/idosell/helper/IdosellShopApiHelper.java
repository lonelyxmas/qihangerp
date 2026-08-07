package cn.qihangerp.open.idosell.helper;

import cn.qihangerp.open.common.ApiResultVo;
import cn.qihangerp.open.common.ApiResultVoEnum;
import cn.qihangerp.open.idosell.response.ShopCategory;
import cn.qihangerp.open.idosell.response.SizeAttributeResponse;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class IdosellShopApiHelper {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * 获取店铺分类
     * @param shopDomain 商店域名
     * @param apiKey API密钥
     * @return API响应
     */
    public ApiResultVo<ShopCategory> getShopCategory(String shopDomain, String apiKey) throws IOException {

        String result = getShopCategoryString(shopDomain, apiKey);
        if(!StringUtils.hasText(result)){
            return ApiResultVo.error(ApiResultVoEnum.ApiException,"api request error");
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        Integer resultsNumberPage = jsonObject.getInteger("resultsNumberPage");
        List<ShopCategory> orderList = JSONArray.parseArray(jsonObject.getString("categories"), ShopCategory.class);
        return ApiResultVo.success(orderList.size(),orderList);
    }

    /**
     * 获取店铺Size属性
     * @param shopDomain 商店域名
     * @param apiKey API密钥
     * @return API响应
     */
    public SizeAttributeResponse getSizeAttr(String shopDomain, String apiKey) throws IOException {

        String result = getSizeAttrString(shopDomain, apiKey);
        if(!StringUtils.hasText(result)){
            return null;
        }
        try {
            return JSONObject.parseObject(result, SizeAttributeResponse.class);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 获取商品列表（使用ProductListParams参数）
     * @param shopDomain 商店域名
     * @param apiKey API密钥
     * @return API响应
     */
    public String getShopCategoryString(String shopDomain, String apiKey ) throws IOException {
        // 构建请求体
        Map<String, Object> requestMap = new HashMap<>();
//        requestMap.put("params", params.toMap());

//        String jsonBody = objectMapper.writeValueAsString(requestMap);
//        log.info("构建的请求体: {}", jsonBody);
//
//        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
//        urlBuilder.addQueryParameter("pageNumber", request.getPageNumber().toString());
//        urlBuilder.addQueryParameter("pageSize", request.getPageSize().toString());
//        // 发送请求
//        RequestBody body = RequestBody.create(
//                MediaType.parse("application/json; charset=utf-8"),
//                jsonBody
//        );
//
//        MediaType mediaType = MediaType.parse("application/json");
////        RequestBody body = RequestBody.create(mediaType, "{\"params\":{\"ordersRange\":{\"ordersDateRange\":{\"ordersDateType\":\"modified\",\"ordersDateBegin\":\"2025-03-09 01:10:35\"}},\"resultsPage\":0,\"resultsLimit\":20,\"ordersBy\":[{\"elementName\":\"id\",\"sortDirection\":\"DESC\"}]}}");
////        RequestBody body = RequestBody.create(mediaType, jsonBody);


        String apiUrl = String.format("https://%s/api/admin/v4/products/categories", shopDomain);
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("X-API-KEY", apiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("accept", "application/json")
//                .post(body)
                .build();

        log.info("发送请求到URL: {}", request.url());
        log.info("请求头: {}", request.headers());
//        log.info("请求体: {}", jsonBody);

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : null;
            if (!response.isSuccessful()) {
                log.error("API请求失败, 状态码: {}, 响应体: {}", response.code(), responseBody);
                throw new IOException("Unexpected response code: " + response.code() + ", body: " + responseBody);
            }
            return responseBody;
        }
    }

    public String getSizeAttrString(String shopDomain, String apiKey ) throws IOException {
        // 构建请求体
        Map<String, Object> requestMap = new HashMap<>();
        String apiUrl = String.format("https://%s/api/admin/v4/sizes/sizes", shopDomain);
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("X-API-KEY", apiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("accept", "application/json")
//                .post(body)
                .build();

//        log.info("发送请求到URL: {}", request.url());
//        log.info("请求头: {}", request.headers());
//        log.info("请求体: {}", jsonBody);

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : null;
            if (!response.isSuccessful()) {
                log.error("API请求失败, 状态码: {}, 响应体: {}", response.code(), responseBody);
                throw new IOException("Unexpected response code: " + response.code() + ", body: " + responseBody);
            }
            return responseBody;
        }
    }
}
