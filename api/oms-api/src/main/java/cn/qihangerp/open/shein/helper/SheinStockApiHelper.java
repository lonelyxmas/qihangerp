package cn.qihangerp.open.shein.helper;

import cn.qihangerp.common.ResultVo;
import cn.qihangerp.open.shein.helper.model.*;
import cn.qihangerp.open.shein.helper.model.UpdateGoodsInventoryRequest;
import cn.qihangerp.open.shein.helper.response.SheinStockResponse;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SheinStockApiHelper {

    private static final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build();
    
    private static final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * 
     *
     * @param  spuNameList 
     * @return SKU
     * @throws IOException API
     */
    public static SheinStockResponse queryStockList(String serverUrl, String appKey, String appSecret, List<String> spuNameList) throws IOException {
        String url = serverUrl + "/open-api/stock/stock-query";

        // ?
        Headers headers = SheinSignatureHelper.createHeaders(appKey,appSecret,url);

        // ?
        Map<String,Object> params = new HashMap<>();
        params.put("warehouseType","3");
        params.put("spuNameList",spuNameList.stream().toArray());

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

            return objectMapper.readValue(responseBody, SheinStockResponse.class);
        }
    }

    /**
     * 
     *
     * @param request 
     * @return SKU
     * @throws IOException API
     */
    public static ResultVo syncGoodsInventory(UpdateGoodsInventoryRequest request) throws IOException {
        String url = request.getUrl() + "/open-api/goods/stock-update";

        // ?
        Headers headers = SheinSignatureHelper.createHeaders(request.getAppKey(),request.getAppSecret(),url);

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
                JSONObject jsonObject = JSONObject.parseObject(responseBody);
//                throw new IOException("Unexpected response code: " + response.code() + ", body: " + responseBody);
                log.error("Unexpected response code: " + response.code() + ", body: " + responseBody);
                return ResultVo.error(jsonObject.getString("msg"));
            }
//            return objectMapper.readValue(responseBody, UpdateGoodsInventoryResponse.class);
            return ResultVo.success();
        }
    }
} 