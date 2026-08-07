package cn.qihangerp.open.idosell.helper;

import cn.qihangerp.open.common.ApiResultVo;
import cn.qihangerp.open.common.ApiResultVoEnum;
import cn.qihangerp.open.idosell.response.Order;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class IdosellOrderApiHelper {
    
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 构建商品列表查询参数
     */
    public static class OrderListParams {
        // 排序方向常量
        public static final String SORT_ASC = "ASC";
        public static final String SORT_DESC = "DESC";

        // 可范围查询字段
        public static class OrdersDateType {
            // 修改时间
            public static final String modified = "modified";
        }
        
        private Integer resultsPage = 0;        // 页码，从0开始
        private Integer resultsLimit = 100;      // 每页数量，建议10-50
        private List<Map<String,String>> sortFields = new ArrayList<>();
        private Map<String,Object> ordersDateRange = new HashMap<>();
//        private String sortBy;                  // 排序字段
//        private String sortDirection;           // 排序方向 (ASC/DESC)
        
        // Builder模式构建器
        public static class Builder {
            private OrderListParams params = new OrderListParams();
            
            public Builder page(Integer page) {
                params.resultsPage = page;
                return this;
            }
            
            public Builder limit(Integer limit) {
                params.resultsLimit = limit;
                return this;
            }

            /**
             * 设置排序
             * @param field 排序字段，建议使用SortFields中的常量
             * @param ascending true表示升序，false表示降序
             */
            public Builder sortBy(String field, boolean ascending) {
                Map<String,String> sortField = new HashMap<>();
                sortField.put("elementName", field);
                sortField.put("sortDirection", ascending ? SORT_ASC : SORT_DESC);
                params.sortFields.add(sortField);
                return this;
            }

            /**
             * 设置订单范围
             * @param
             * @param
             */
            public Builder ordersDateRange(String ordersDateType, LocalDateTime startTime,LocalDateTime  endTime) {
//                Map<String,Object> ordersDateRange = new HashMap<>();
                Map<String,String> ordersDateRangeMap = new HashMap<>();
                ordersDateRangeMap.put("ordersDateType", ordersDateType);

                if(startTime!=null) {
                    ordersDateRangeMap.put("ordersDateBegin", startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
                if(endTime!=null) {
                    ordersDateRangeMap.put("ordersDateEnd", endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
//                ordersDateRange.put("ordersDateRange", ordersDateRangeMap);
                params.ordersDateRange.put("ordersDateRange", ordersDateRangeMap);
                return this;
            }
            
            public OrderListParams build() {
                return params;
            }
        }
        
        public Map<String, Object> toMap() throws JsonProcessingException {
            Map<String, Object> paramsMap = new HashMap<>();
//            paramsMap.put("ordersStatuses",new String[]{"new"});
            // 基本参数
            paramsMap.put("resultsPage", resultsPage);
            paramsMap.put("resultsLimit", resultsLimit);
            ObjectMapper objectMapper = new ObjectMapper();
            if (sortFields != null&&!sortFields.isEmpty()) paramsMap.put("ordersBy",  sortFields);
            if(ordersDateRange!=null&&!ordersDateRange.isEmpty()) paramsMap.put("ordersRange", ordersDateRange);
//            if (sortDirection != null) paramsMap.put("sortDirection", sortDirection);
//            if (sortBy != null) paramsMap.put("sortBy", sortBy);
//            if (sortDirection != null) paramsMap.put("sortDirection", sortDirection);
            return paramsMap;
        }
    }
    
    /**
     * 获取商品列表
     * @param shopDomain 商店域名
     * @param apiKey API密钥
     * @param page 页码（从0开始）
     * @param limit 每页数量
     * @return API响应
     */
    public ApiResultVo<Order> getOrderList(String shopDomain, String apiKey, Integer page, Integer limit, LocalDateTime startTime, LocalDateTime  endTime) throws IOException {
        OrderListParams params = new OrderListParams.Builder()
            .page(page)
            .limit(limit)
                .sortBy("id",false)
                .ordersDateRange(OrderListParams.OrdersDateType.modified,startTime,endTime)
            .build();
        String result = getOrderList(shopDomain, apiKey, params);
        if(!StringUtils.hasText(result)){
            return ApiResultVo.error(ApiResultVoEnum.ApiException,"api request error");
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        Integer resultsNumberPage = jsonObject.getInteger("resultsNumberPage");
        List<Order> orderList = JSONArray.parseArray(jsonObject.getString("Results"), Order.class);
        return ApiResultVo.success(orderList.size(),orderList);
    }

    /**
     * 获取商品列表（使用ProductListParams参数）
     * @param shopDomain 商店域名
     * @param apiKey API密钥
     * @param params 查询参数
     * @return API响应
     */
    public String getOrderList(String shopDomain, String apiKey, OrderListParams params) throws IOException {
        // 构建请求体
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("params", params.toMap());
        
        String jsonBody = objectMapper.writeValueAsString(requestMap);
        log.info("构建的请求体: {}", jsonBody);

        // 发送请求
        RequestBody body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            jsonBody
        );

        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(mediaType, "{\"params\":{\"ordersRange\":{\"ordersDateRange\":{\"ordersDateType\":\"modified\",\"ordersDateBegin\":\"2025-03-09 01:10:35\"}},\"resultsPage\":0,\"resultsLimit\":20,\"ordersBy\":[{\"elementName\":\"id\",\"sortDirection\":\"DESC\"}]}}");
//        RequestBody body = RequestBody.create(mediaType, jsonBody);


        String apiUrl = String.format("https://%s/api/admin/v4/orders/orders/get", shopDomain);
        Request request = new Request.Builder()
            .url(apiUrl)
            .addHeader("X-API-KEY", apiKey)
            .addHeader("Content-Type", "application/json")
            .addHeader("accept", "application/json")
            .post(body)
            .build();

        log.info("发送请求到URL: {}", request.url());
        log.info("请求头: {}", request.headers());
        log.info("请求体: {}", jsonBody);
            
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