package cn.qihangerp.open.idosell.helper;

import cn.qihangerp.common.ResultVo;
import cn.qihangerp.open.idosell.response.StockData;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class IdosellStockApiHelper {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * 获取商品列表
     * @param shopDomain 商店域名
     * @param apiKey API密钥
     * @param productIds
     * @return API响应
     */
    public ResultVo<StockData> getStockList(String shopDomain, String apiKey, List<String> productIds) throws IOException {

        String result = getStockListString(shopDomain, apiKey,  productIds);
        if(!StringUtils.hasText(result)){
            return ResultVo.error("API访问错误");
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        if(jsonObject.getBoolean("is_errors")){
            return ResultVo.error("API响应错误");
        }
        JSONArray results = jsonObject.getJSONArray("results");
        if(results!=null && results.size()>0){
            List<StockData> stockDataList = new ArrayList<>();
            for (int i = 0; i < results.size(); i++) {
                JSONArray stocks = results.getJSONObject(i).getJSONObject("quantities").getJSONArray("stocks");
                String productId = results.getJSONObject(i).getJSONObject("ident").getString("identValue");
                for (int j = 0; j < stocks.size(); j++) {
                    JSONObject stockObject = stocks.getJSONObject(j);
                    Integer stockId = stockObject.getInteger("stock_id");
                    JSONArray sizes = stockObject.getJSONArray("sizes");
                    for(int k=0;k<sizes.size();k++){
                        JSONObject sizeObject = sizes.getJSONObject(k);
                        String sizeId = sizeObject.getString("size_id");
                        Integer quantity = sizeObject.getInteger("quantity");
                        String sizeName = sizeObject.getString("size_name");
                        String sizeCode = sizeObject.getString("product_sizecode");

                        StockData stockData = new StockData();
                        stockData.setStockId(stockId);
                        stockData.setProductId(productId);
                        stockData.setSizeId(sizeId);
                        stockData.setSizeName(sizeName);
                        stockData.setQuantity(quantity);
                        stockData.setProductSizeCode(sizeCode);
                        stockDataList.add(stockData);
                    }
                }

            }
            return ResultVo.success(stockDataList.size(),stockDataList);
        }else{
            return ResultVo.success(0,new ArrayList<>());
        }


    }

    /**
     * 获取商品列表（使用ProductListParams参数）
     * @param shopDomain 商店域名
     * @param apiKey API密钥
     * @param productIds ProductId
     * @return API响应
     */
    protected String getStockListString(String shopDomain, String apiKey, List<String> productIds) throws IOException {
        // 构建请求体
        List<Map<String,String>> products = new ArrayList<>();
        for(String productId : productIds){
            Map<String, String> params = new HashMap<>();
            params.put("identType", "id");
            params.put("identValue", productId);
            products.add(params);
        }

        String paramStr  = objectMapper.writeValueAsString(products);

        String apiUrl = String.format("https://%s/api/admin/v4/products/stocks", shopDomain);

        HttpUrl.Builder urlBuilder = HttpUrl.parse(apiUrl).newBuilder();
        urlBuilder.addQueryParameter("products", paramStr.substring(1,paramStr.length()-1));

        apiUrl = urlBuilder.build().toString();
        // 构建请求
        Request request = new Request.Builder()
            .url(apiUrl)
            .addHeader("X-API-KEY", apiKey)
            .addHeader("Content-Type", "application/json")
            .addHeader("accept", "application/json")
            .build();

//        log.info("发送请求到URL: {}", request.url());

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : null;
            if (!response.isSuccessful()) {
                log.error("API请求失败, 状态码: {}, 响应体: {}", response.code(), responseBody);
                throw new IOException("Unexpected response code: " + response.code() + ", body: " + responseBody);
            }
            return responseBody;
        }
    }

    /**
     * 更新商品库存数量
     * @param shopDomain 商店域名
     * @param apiKey API密钥
     * @param productSizeCodeExternal 商品ID
     * @param stockId 仓库ID
     * @param quantity 新的库存数量
     * @return 更新结果
     */
    public ResultVo updateProductStock(String shopDomain, String apiKey, String productSizeCodeExternal, Integer stockId, Integer quantity) throws IOException {
        String apiUrl = String.format("https://%s/api/admin/v4/products/stockQuantity", shopDomain);

        // 构建请求体
        List<Map<String, Object>> stockDataList = new ArrayList<>();
        Map<String, Object> stockData = new HashMap<>();
        stockData.put("productSizeCodeExternal", productSizeCodeExternal);
        stockData.put("stockId", stockId);
        stockData.put("productSizeQuantity", quantity);
        stockDataList.add(stockData);

        Map<String, List<Map<String, Object>> > products = new HashMap<>();
        products.put("products", stockDataList);
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("params", products);

        String jsonBody = objectMapper.writeValueAsString(requestMap);

        // 发送请求
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonBody
        );

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("X-API-KEY", apiKey)
                .addHeader("Content-Type", "application/json")
                .put(body)
                .build();

        log.info("发送库存更新请求到URL: {}", request.url());
        log.info("请求头: {}", request.headers());
        log.info("请求体: {}", jsonBody);

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : null;
            if (!response.isSuccessful()) {
                log.error("更新库存失败, 状态码: {}, 响应体: {}", response.code(), responseBody);
                throw new IOException("Unexpected response code: " + response.code() + ", body: " + responseBody);
            }
            JSONObject jsonObject = JSONObject.parseObject(responseBody);
            if(jsonObject.get("errors")!=null){
                log.error("更新库存失败,｛｝", jsonObject.getString("errors"));
                return ResultVo.error(jsonObject.getString("errors"));
            }
            log.info("库存更新成功");
            return ResultVo.success(responseBody);
        }
    }

}