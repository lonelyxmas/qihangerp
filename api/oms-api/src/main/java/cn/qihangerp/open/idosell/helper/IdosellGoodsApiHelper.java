package cn.qihangerp.open.idosell.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class IdosellGoodsApiHelper {
    
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 构建商品列表查询参数
     */
    public static class ProductListParams {
        // 排序方向常量
        public static final String SORT_ASC = "ASC";
        public static final String SORT_DESC = "DESC";
        
        // 可排序字段常量
        public static class SortFields {
            // 基本信息排序
            public static final String PRODUCT_ID = "productId";                   // 商品ID
            public static final String PRODUCT_DISPLAYED_CODE = "productDisplayedCode"; // 商品编码
            public static final String PRODUCT_NAME = "productName";               // 商品名称
            public static final String PRODUCT_RETAIL_PRICE = "productRetailPrice"; // 零售价格
            public static final String PRODUCT_WHOLESALE_PRICE = "productWholesalePrice"; // 批发价格
            
            // 时间相关排序
            public static final String PRODUCT_ADDED_DATE = "productAddedDate";     // 添加时间
            public static final String PRODUCT_UPDATED_DATE = "productUpdatedDate"; // 更新时间
            
            // 库存相关排序
            public static final String PRODUCT_STOCK_QUANTITY = "productStockQuantity"; // 库存数量
            
            // 分类相关排序
            public static final String CATEGORY_ID = "categoryId";                 // 分类ID
            public static final String PRODUCER_ID = "producerId";                 // 制造商ID
        }
        
        private Integer resultsPage = 0;        // 页码，从0开始
        private Integer resultsLimit = 20;      // 每页数量，建议10-50
        private String productId;               // 商品ID
        private String productDisplayedCode;    // 商品显示编码
        private String productIsDeleted;        // 是否删除 (y/n)
        private Long producerId;                // 制造商ID
        private Long categoryId;                // 分类ID
        private List<Long> categoryIds;         // 分类ID列表
        private String productName;             // 商品名称
        private String langId;                  // 语言ID，如 pol-波兰语, eng-英语
        private Double priceFrom;               // 价格范围-开始
        private Double priceTo;                 // 价格范围-结束
        private String sortBy;                  // 排序字段
        private String sortDirection;           // 排序方向 (ASC/DESC)
        
        // Builder模式构建器
        public static class Builder {
            private ProductListParams params = new ProductListParams();
            
            public Builder page(Integer page) {
                params.resultsPage = page;
                return this;
            }
            
            public Builder limit(Integer limit) {
                params.resultsLimit = limit;
                return this;
            }
            
            public Builder productId(String productId) {
                params.productId = productId;
                return this;
            }
            
            public Builder displayedCode(String code) {
                params.productDisplayedCode = code;
                return this;
            }
            
            public Builder isDeleted(boolean isDeleted) {
                params.productIsDeleted = isDeleted ? "y" : "n";
                return this;
            }
            
            public Builder producerId(Long id) {
                params.producerId = id;
                return this;
            }
            
            public Builder categoryId(Long id) {
                params.categoryId = id;
                params.categoryIds = Arrays.asList(id);  // 同时设置categoryIds
                return this;
            }
            
            public Builder productName(String name) {
                params.productName = name;
                return this;
            }
            
            public Builder language(String langId) {
                params.langId = langId;
                return this;
            }
            
            public Builder priceRange(Double from, Double to) {
                params.priceFrom = from;
                params.priceTo = to;
                return this;
            }
            
            /**
             * 设置排序
             * @param field 排序字段，建议使用SortFields中的常量
             * @param ascending true表示升序，false表示降序
             */
            public Builder sortBy(String field, boolean ascending) {
                params.sortBy = field;
                params.sortDirection = ascending ? SORT_ASC : SORT_DESC;
                return this;
            }
            
            /**
             * 按商品ID排序
             * @param ascending true表示升序，false表示降序
             */
            public Builder sortByProductId(boolean ascending) {
                return sortBy(SortFields.PRODUCT_ID, ascending);
            }
            
            /**
             * 按价格排序
             * @param ascending true表示升序，false表示降序
             * @param useWholesalePrice true使用批发价，false使用零售价
             */
            public Builder sortByPrice(boolean ascending, boolean useWholesalePrice) {
                return sortBy(useWholesalePrice ? SortFields.PRODUCT_WHOLESALE_PRICE : SortFields.PRODUCT_RETAIL_PRICE, ascending);
            }
            
            /**
             * 按添加时间排序
             * @param ascending true表示升序，false表示降序
             */
            public Builder sortByAddedDate(boolean ascending) {
                return sortBy(SortFields.PRODUCT_ADDED_DATE, ascending);
            }
            
            /**
             * 按更新时间排序
             * @param ascending true表示升序，false表示降序
             */
            public Builder sortByUpdatedDate(boolean ascending) {
                return sortBy(SortFields.PRODUCT_UPDATED_DATE, ascending);
            }
            
            /**
             * 按库存数量排序
             * @param ascending true表示升序，false表示降序
             */
            public Builder sortByStockQuantity(boolean ascending) {
                return sortBy(SortFields.PRODUCT_STOCK_QUANTITY, ascending);
            }
            
            public ProductListParams build() {
                return params;
            }
        }
        
        public Map<String, Object> toMap() {
            Map<String, Object> paramsMap = new HashMap<>();
            
            // 基本参数
            paramsMap.put("resultsPage", resultsPage);
            paramsMap.put("resultsLimit", resultsLimit);
            //paramsMap.put("returnProducts", "true");
            
            // 分类ID处理
            if (categoryIds != null && !categoryIds.isEmpty()) {
                List<Map<String, String>> categories = new ArrayList<>();
                for (Long categoryId : categoryIds) {
                    Map<String, String> category = new HashMap<>();
                    category.put("categoryId", String.valueOf(categoryId));
                    categories.add(category);
                }
                paramsMap.put("categories", categories);
            }
            
            // 构建过滤条件
            Map<String, Object> filtersMap = new HashMap<>();
            
            // 其他过滤条件
            if (productId != null) {
                Map<String, Object> productFilter = new HashMap<>();
                productFilter.put("type", "equal");
                productFilter.put("value", productId);
                filtersMap.put("productId", productFilter);
            }
            if (productDisplayedCode != null) {
                Map<String, Object> codeFilter = new HashMap<>();
                codeFilter.put("type", "equal");
                codeFilter.put("value", productDisplayedCode);
                filtersMap.put("productDisplayedCode", codeFilter);
            }
            if (productIsDeleted != null) {
                Map<String, Object> deletedFilter = new HashMap<>();
                deletedFilter.put("type", "equal");
                deletedFilter.put("value", productIsDeleted);
                filtersMap.put("productIsDeleted", deletedFilter);
            }
            if (producerId != null) {
                Map<String, Object> producerFilter = new HashMap<>();
                producerFilter.put("type", "equal");
                producerFilter.put("value", producerId);
                filtersMap.put("producerId", producerFilter);
            }
            if (productName != null) {
                Map<String, Object> nameFilter = new HashMap<>();
                nameFilter.put("type", "like");
                nameFilter.put("value", productName);
                filtersMap.put("productName", nameFilter);
            }
            
            // 只有在有过滤条件时才添加filters
            if (!filtersMap.isEmpty()) {
                paramsMap.put("filters", filtersMap);
            }
            
            // 其他参数
            if (langId != null) paramsMap.put("langId", langId);
            if (priceFrom != null) paramsMap.put("priceFrom", priceFrom);
            if (priceTo != null) paramsMap.put("priceTo", priceTo);
            if (sortBy != null) paramsMap.put("sortBy", sortBy);
            if (sortDirection != null) paramsMap.put("sortDirection", sortDirection);
            
            return paramsMap;
        }
    }
    




//    /**
//     * 更新商品库存数量
//     * @param shopDomain 商店域名
//     * @param apiKey API密钥
//     * @param productId 商品ID
//     * @param stockId 仓库ID
//     * @param sizeId 尺码ID
//     * @param quantity 新的库存数量
//     * @return 更新结果
//     */
//    public String updateProductStock(String shopDomain, String apiKey, Integer productId,
//            Integer stockId, String sizeId, Integer quantity) throws IOException {
//        String apiUrl = String.format("https://%s/api/admin/v4/products/stockQuantity", shopDomain);
//
//        // 构建请求体
//        Map<String, Object> stockData = new HashMap<>();
//        stockData.put("productId", productId);
//        stockData.put("stockId", stockId);
//        stockData.put("sizeId", sizeId);
//        stockData.put("quantity", quantity);
//
//        Map<String, Object> requestMap = new HashMap<>();
//        requestMap.put("params", stockData);
//
//        String jsonBody = objectMapper.writeValueAsString(requestMap);
//
//        // 发送请求
//        RequestBody body = RequestBody.create(
//            MediaType.parse("application/json; charset=utf-8"),
//            jsonBody
//        );
//
//        Request request = new Request.Builder()
//            .url(apiUrl)
//            .addHeader("X-API-KEY", apiKey)
//            .addHeader("Content-Type", "application/json")
//            .put(body)
//            .build();
//
//        log.info("发送库存更新请求到URL: {}", request.url());
//        log.info("请求头: {}", request.headers());
//        log.info("请求体: {}", jsonBody);
//
//        try (Response response = client.newCall(request).execute()) {
//            String responseBody = response.body() != null ? response.body().string() : null;
//            if (!response.isSuccessful()) {
//                log.error("更新库存失败, 状态码: {}, 响应体: {}", response.code(), responseBody);
//                throw new IOException("Unexpected response code: " + response.code() + ", body: " + responseBody);
//            }
//            log.info("库存更新成功");
//            return responseBody;
//        }
//    }


} 