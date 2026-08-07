package cn.qihangerp.open.idosell.helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.alibaba.fastjson2.JSON;

import cn.qihangerp.open.idosell.helper.request.PublishProductRequest;
import cn.qihangerp.open.idosell.helper.response.PublishProductResponse;
import cn.qihangerp.open.idosell.model.ProductResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IdosellApiHelper {
    
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
            public static final String ID = "id";                   // 商品ID
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
        private Integer categoryId;                // 分类ID
        private List<Integer> categoryIds;         // 分类ID列表
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
            
            public Builder categoryId(Integer id) {
                params.categoryId = id;
                params.categoryIds = Arrays.asList(id);  // 同时设置categoryIds
                return this;
            }
            public Builder categoryIds(List<Integer> categoryIds) {
//                params.categoryId = id;
                params.categoryIds = categoryIds;  // 同时设置categoryIds
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
            public Builder sortById(boolean ascending) {
                return sortBy(SortFields.ID, ascending);
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
                for (Integer categoryId : categoryIds) {
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
    
    /**
     * 获取商品列表
     * @param shopDomain 商店域名
     * @param apiKey API密钥
     * @param page 页码（从0开始）
     * @param limit 每页数量
     * @return API响应
     */
    public String getProductList(String shopDomain, String apiKey, Integer page, Integer limit) throws IOException {
        ProductListParams params = new ProductListParams.Builder()
            .page(page)
            .limit(limit)
            .build();
        return getProductList(shopDomain, apiKey, params);
    }

    /**
     * 获取商品列表（使用ProductListParams参数）
     * @param shopDomain 商店域名
     * @param apiKey API密钥
     * @param params 查询参数
     * @return API响应
     */
    public String getProductList(String shopDomain, String apiKey, ProductListParams params) throws IOException {
        // 构建请求体
        Map<String, Object> requestMap = new HashMap<>();
        Map<String, Object> map = params.toMap();
        map.put("productIsAvailable","y");
        map.put("productAvailableInStocks","{\"productIsAvailableInStocks\": \"y\"");

        requestMap.put("params", map);
        
        String jsonBody = objectMapper.writeValueAsString(requestMap);
//        log.info("构建的请求体: {}", jsonBody);

        // 发送请求
        RequestBody body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            jsonBody
        );

        String apiUrl = String.format("https://%s/api/admin/v4/products/products/get", shopDomain);
        Request request = new Request.Builder()
            .url(apiUrl)
            .addHeader("X-API-KEY", apiKey)
            .addHeader("Content-Type", "application/json")
            .addHeader("accept", "application/json")
            .post(body)
            .build();

//        log.info("发送请求到URL: {}", request.url());
//        log.info("请求头: {}", request.headers());
//        log.info("请求体: {}", jsonBody);
        log.info("请求第{}页", params.resultsPage);

            
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : null;
            if (!response.isSuccessful()) {
                log.error("API请求失败, 状态码: {}, 响应体: {}", response.code(), responseBody);
                throw new IOException("Unexpected response code: " + response.code() + ", body: " + responseBody);
            }
            return responseBody;
        }
    }

    public String getProductDetail(String shopDomain, String apiKey, String productId) throws IOException {
        String apiUrl = String.format("https://%s/api/admin/v4/products/%s", shopDomain, productId);
        
        Request request = new Request.Builder()
            .url(apiUrl)
            .addHeader("X-API-KEY", apiKey)
            .addHeader("Content-Type", "application/json")
            .get()
            .build();
            
        log.info("发送请求到URL: {}", request.url());
        log.info("请求头: {}", request.headers());
            
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
     * 发布商品
     *
     * @param shopDomain
     * @param apiKey
     * @return
     * @throws IOException
     */
    public PublishProductResponse publishProduct(String shopDomain, String apiKey,
        PublishProductRequest publishProductRequest) throws IOException {

        String requestBodyContent = JSON.toJSONString(publishProductRequest);
        RequestBody body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            requestBodyContent
        );
        String apiUrl = String.format("https://%s/api/admin/v4/products/products", shopDomain);
        Request request = new Request.Builder()
            .url(apiUrl)
            .addHeader("X-API-KEY", apiKey)
            .addHeader("Content-Type", "application/json")
            .addHeader("accept", "application/json")
            .post(body)
            .build();

        printCurlCommand(apiUrl, apiKey, requestBodyContent);

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : null;
            if (!response.isSuccessful()) {
                log.error("API请求失败, 状态码: {}, 响应体: {}", response.code(), responseBody);
                throw new IOException("Unexpected response code: " + response.code() + ", body: " + responseBody);
            }
            return JSON.parseObject(responseBody, PublishProductResponse.class);
        }
    }

    /**
     * 打印cURL命令
     *
     * @param url
     * @param apiKey
     * @param requestBodyContent
     */
    private void printCurlCommand(String url, String apiKey, String requestBodyContent) {
        StringBuilder curlCmd = new StringBuilder("curl -X POST ");
        curlCmd.append("\"").append(url).append("\" ");
        curlCmd.append("-H \"X-API-KEY: ").append(apiKey).append("\" ");
        curlCmd.append("-H \"Content-Type: application/json\" ");
        curlCmd.append("-H \"accept: application/json\" ");
        curlCmd.append("-d '").append(requestBodyContent).append("' ");

        log.info(curlCmd.toString());
    }

    /**
     * 获取有库存的商品列表
     * @param shopDomain 商店域名
     * @param apiKey API密钥
     * @param page 页码（从0开始）
     * @param limit 每页数量
     * @return 商品列表响应
     */
    public ProductResponse getProductsWithStock(String shopDomain, String apiKey, Integer page, Integer limit) throws IOException {
        String response = getProductList(shopDomain, apiKey, page, limit);
        ObjectMapper mapper = new ObjectMapper();
        ProductResponse productResponse = mapper.readValue(response, ProductResponse.class);
        
        // 过滤出有库存的商品
        List<ProductResponse.Product> productsWithStock = productResponse.getResults().stream()
            .filter(product -> hasStock(product))
            .collect(Collectors.toList());
        
        productResponse.setResults(productsWithStock);
        return productResponse;
    }
    
    /**
     * 检查商品是否有库存
     * @param product 商品信息
     * @return true如果有库存，false如果无库存
     */
    private boolean hasStock(ProductResponse.Product product) {
        if (product.getProductStocksData() == null 
            || product.getProductStocksData().getProductStocksQuantities() == null) {
            return false;
        }
        
        return product.getProductStocksData().getProductStocksQuantities().stream()
            .flatMap(stockQuantity -> stockQuantity.getProductSizesData().stream())
            .anyMatch(sizeData -> {
                Integer quantity = sizeData.getProductSizeQuantity();
                ProductResponse.ProductSizeReservations reservations = sizeData.getProductSizeReservations();
                
                // 计算实际可用库存（总库存减去所有预留）
                int reserved = 0;
                if (reservations != null) {
                    reserved = Stream.of(
                        reservations.getProductSizeReservationAdhoc(),
                        reservations.getProductSizeReservationAuction(),
                        reservations.getProductSizeReservationClient(),
                        reservations.getProductSizeReservationOrder(),
                        reservations.getProductSizeReservationRetail(),
                        reservations.getProductSizeReservationWholesale()
                    )
                    .filter(Objects::nonNull)
                    .mapToInt(Integer::intValue)
                    .sum();
                }
                
                return quantity != null && (quantity - reserved) > 0;
            });
    }

    /**
     * 遍历所有页面获取有库存的商品，按库存数量从多到少排序
     * @param shopDomain 商店域名
     * @param apiKey API密钥
     * @param pageSize 每页数量（建议10-50之间）
     * @return 所有有库存的商品列表
     */
    public List<ProductResponse.Product> getAllProductsWithStock(String shopDomain, String apiKey, int pageSize) throws IOException {
        List<ProductResponse.Product> allProducts = new ArrayList<>();
        int currentPage = 0;
        boolean hasMoreProducts = true;
        
        log.info("开始获取所有有库存商品...");
        
        // 构建查询参数，按库存数量降序排序
        ProductListParams params = new ProductListParams.Builder()
            .limit(pageSize)
            .sortByStockQuantity(false)  // false表示降序，库存多的在前面
            .build();
        
        while (hasMoreProducts) {
            try {
                log.info("----------------------------------------");
                log.info("正在获取第 {} 页数据...", currentPage + 1);
                params = new ProductListParams.Builder()
                    .page(currentPage)
                    .limit(pageSize)
                    .sortByStockQuantity(false)
                    .build();
                
                String response = getProductList(shopDomain, apiKey, params);
                ProductResponse productResponse = objectMapper.readValue(response, ProductResponse.class);
                
                // 过滤有库存的商品
                List<ProductResponse.Product> productsWithStock = productResponse.getResults().stream()
                    .filter(this::hasStock)
                    .collect(Collectors.toList());
                
                // 如果没有找到有库存的商品，说明后面也不会有了（因为已经按库存量降序排序）
                if (productsWithStock.isEmpty()) {
                    log.info("没有找到更多有库存商品，停止查询");
                    hasMoreProducts = false;
                } else {
                    allProducts.addAll(productsWithStock);
                    log.info("第 {} 页发现 {} 个有库存商品，当前总共收集到 {} 个商品",
                        currentPage + 1, productsWithStock.size(), allProducts.size());
                    
                    // 打印当前页每个商品的库存信息
                    log.info("当前页商品库存明细：");
                    productsWithStock.forEach(product -> {
                        // 获取商品名称（优先获取英文名称）
                        String productName = product.getProductDescriptionsLangData().stream()
                            .filter(desc -> "eng".equals(desc.getLangId()))
                            .map(ProductResponse.ProductDescription::getProductName)
                            .findFirst()
                            .orElseGet(() -> product.getProductDescriptionsLangData().get(0).getProductName());
                        
                        int totalStock = calculateTotalStock(product);
                        log.info("商品ID: {}, 名称: {}, 总库存: {}", 
                            product.getProductId(), productName, totalStock);
                        
                        // 打印各个仓库和尺码的库存明细
                        if (product.getProductStocksData() != null && 
                            product.getProductStocksData().getProductStocksQuantities() != null) {
                            product.getProductStocksData().getProductStocksQuantities().forEach(stock -> {
                                log.info("  仓库ID: {}", stock.getStockId());
                                stock.getProductSizesData().forEach(size -> {
                                    // 计算实际可用库存
                                    int available = calculateAvailableStock(size);
                                    log.info("    尺码: {}, 总数量: {}, 可用数量: {}", 
                                        size.getSizePanelName(), 
                                        size.getProductSizeQuantity(),
                                        available);
                                });
                            });
                        }
                        log.info("----------------------------------------");
                    });
                }
                
                // 处理速率限制，避免请求过快
                Thread.sleep(1000);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("线程被中断", e);
                break;
            } catch (Exception e) {
                log.error("获取第 {} 页数据时发生错误: {}", currentPage + 1, e.getMessage());
                // 如果发生错误，等待5秒后重试
                try {
                    Thread.sleep(5000);
                    continue; // 重试当前页
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            currentPage++;
        }
        
        // 打印汇总信息
        log.info("========================================");
        log.info("数据获取完成，统计信息：");
        log.info("总共获取页数: {}", currentPage);
        log.info("有库存商品总数: {}", allProducts.size());
        
        // 按库存数量排序（确保最终列表是按库存量从高到低排序的）
        allProducts.sort((p1, p2) -> {
            int stock1 = calculateTotalStock(p1);
            int stock2 = calculateTotalStock(p2);
            return Integer.compare(stock2, stock1);  // 降序排序
        });
        
        return allProducts;
    }
    
    /**
     * 计算单个尺码的可用库存数量
     */
    private int calculateAvailableStock(ProductResponse.ProductSizeData sizeData) {
        Integer quantity = sizeData.getProductSizeQuantity();
        if (quantity == null) return 0;
        
        ProductResponse.ProductSizeReservations reservations = sizeData.getProductSizeReservations();
        if (reservations == null) return quantity;
        
        int reserved = Stream.of(
            reservations.getProductSizeReservationAdhoc(),
            reservations.getProductSizeReservationAuction(),
            reservations.getProductSizeReservationClient(),
            reservations.getProductSizeReservationOrder(),
            reservations.getProductSizeReservationRetail(),
            reservations.getProductSizeReservationWholesale()
        )
        .filter(Objects::nonNull)
        .mapToInt(Integer::intValue)
        .sum();
        
        return Math.max(0, quantity - reserved);
    }
    
    /**
     * 计算商品的总库存数量（所有仓库和尺码的可用库存之和）
     */
    private int calculateTotalStock(ProductResponse.Product product) {
        if (product.getProductStocksData() == null 
            || product.getProductStocksData().getProductStocksQuantities() == null) {
            return 0;
        }
        
        return product.getProductStocksData().getProductStocksQuantities().stream()
            .flatMap(stockQuantity -> stockQuantity.getProductSizesData().stream())
            .mapToInt(sizeData -> {
                Integer quantity = sizeData.getProductSizeQuantity();
                ProductResponse.ProductSizeReservations reservations = sizeData.getProductSizeReservations();
                
                int reserved = 0;
                if (reservations != null) {
                    reserved = Stream.of(
                        reservations.getProductSizeReservationAdhoc(),
                        reservations.getProductSizeReservationAuction(),
                        reservations.getProductSizeReservationClient(),
                        reservations.getProductSizeReservationOrder(),
                        reservations.getProductSizeReservationRetail(),
                        reservations.getProductSizeReservationWholesale()
                    )
                    .filter(Objects::nonNull)
                    .mapToInt(Integer::intValue)
                    .sum();
                }
                
                return quantity != null ? Math.max(0, quantity - reserved) : 0;
            })
            .sum();
    }

    /**
     * 将商品列表保存为JSON文件
     * @param products 商品列表
     * @param filePath 文件保存路径
     */
    public void saveProductsToJson(List<ProductResponse.Product> products, String filePath) throws IOException {
        File file = new File(filePath);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, products);
        log.info("商品数据已保存到文件: {}", filePath);
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

    /**
     * 获取有库存的鞋子商品
     * @param shopDomain 商店域名
     * @param apiKey API密钥
     * @param pageSize 每页数量
     * @param shoesKeywords 鞋子相关关键词
     * @param shoesCategoryIds 鞋子分类ID列表
     * @return 鞋子商品列表
     */
//    public List<ProductResponse.Product> getShoesProductsWithStock(String shopDomain, String apiKey,
//            int pageSize, List<String> shoesKeywords, List<Long> shoesCategoryIds) throws IOException {
//        List<ProductResponse.Product> allShoes = new ArrayList<>();
//        int currentPage = 0;
//        boolean hasMoreProducts = true;
//        int totalProcessed = 0;
//
//        log.info("开始获取鞋子商品数据...");
//        log.info("使用的关键词: {}", shoesKeywords);
//        log.info("使用的分类ID: {}", shoesCategoryIds);
//
//        while (hasMoreProducts) {
//            try {
//                log.info("----------------------------------------");
//                log.info("正在获取第 {} 页数据...", currentPage + 1);
//
//                // 遍历每个分类ID
//                for (Long categoryId : shoesCategoryIds) {
//                    ProductListParams params = new ProductListParams.Builder()
//                        .page(currentPage)
//                        .limit(pageSize)
//                        .categoryId(categoryId)
//                        .sortByStockQuantity(false)  // 按库存量降序
//                        .build();
//
//                    String response = getProductList(shopDomain, apiKey, params);
//                    ProductResponse productResponse = objectMapper.readValue(response, ProductResponse.class);
//
//                    // 过滤出有库存的鞋子
//                    List<ProductResponse.Product> shoesWithStock = productResponse.getResults().stream()
//                        .filter(this::hasStock)  // 首先过滤有库存的
//                        .filter(product -> isShoeProduct(product, shoesKeywords))  // 然后判断是否是鞋子
//                        .collect(Collectors.toList());
//
//                    if (!shoesWithStock.isEmpty()) {
//                        allShoes.addAll(shoesWithStock);
//                        totalProcessed += shoesWithStock.size();
//
//                        // 打印当前获取到的鞋子信息
//                        log.info("分类 {} 第 {} 页发现 {} 个鞋子商品，当前总共收集到 {} 个鞋子",
//                            categoryId, currentPage + 1, shoesWithStock.size(), allShoes.size());
//
//                        // 打印每个鞋子的详细信息
//                        shoesWithStock.forEach(product -> printShoeProductInfo(product));
//                    }
//
//                    // 处理速率限制
//                    Thread.sleep(1000);
//                }
//
//                // 如果当前页没有找到任何商品，或者已经收集够了足够的数量，就停止
//                if (totalProcessed == 0 || allShoes.size() >= 8000) {  // 设置上限为8000
//                    hasMoreProducts = false;
//                    log.info("已达到目标数量或没有更多商品，停止查询");
//                }
//
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                log.error("线程被中断", e);
//                break;
//            } catch (Exception e) {
//                log.error("获取第 {} 页数据时发生错误: {}", currentPage + 1, e.getMessage());
//                try {
//                    Thread.sleep(5000);
//                    continue;
//                } catch (InterruptedException ie) {
//                    Thread.currentThread().interrupt();
//                    break;
//                }
//            }
//
//            currentPage++;
//        }
//
//        // 按库存量排序
//        allShoes.sort((p1, p2) -> {
//            int stock1 = calculateTotalStock(p1);
//            int stock2 = calculateTotalStock(p2);
//            return Integer.compare(stock2, stock1);
//        });
//
//        // 打印汇总信息
//        log.info("========================================");
//        log.info("数据获取完成，统计信息：");
//        log.info("总共获取页数: {}", currentPage);
//        log.info("获取到的鞋子总数: {}", allShoes.size());
//        printStockSummary(allShoes);
//
//        return allShoes;
//    }
//
    /**
     * 判断商品是否是鞋子
     */
    private boolean isShoeProduct(ProductResponse.Product product, List<String> shoesKeywords) {
        if (product.getProductDescriptionsLangData() == null || product.getProductDescriptionsLangData().isEmpty()) {
            return false;
        }
        
        // 检查商品名称是否包含鞋子相关关键词
        return product.getProductDescriptionsLangData().stream()
            .anyMatch(desc -> {
                String name = desc.getProductName().toLowerCase();
                return shoesKeywords.stream()
                    .anyMatch(keyword -> name.contains(keyword.toLowerCase()));
            });
    }
    
    /**
     * 打印鞋子商品信息
     */
    private void printShoeProductInfo(ProductResponse.Product product) {
        // 获取商品名称（优先英文）
        String productName = product.getProductDescriptionsLangData().stream()
            .filter(desc -> "eng".equals(desc.getLangId()))
            .map(ProductResponse.ProductDescription::getProductName)
            .findFirst()
            .orElseGet(() -> product.getProductDescriptionsLangData().get(0).getProductName());
            
        int totalStock = calculateTotalStock(product);
        
        log.info("----------------------------------------");
        log.info("鞋子信息：");
        log.info("ID: {}", product.getProductId());
        log.info("名称: {}", productName);
        log.info("总库存: {}", totalStock);
        log.info("分类ID: {}", product.getCategoryId());
        
        // 打印各个尺码的库存
        if (product.getProductStocksData() != null && 
            product.getProductStocksData().getProductStocksQuantities() != null) {
            log.info("尺码库存明细：");
            product.getProductStocksData().getProductStocksQuantities().forEach(stock -> {
                stock.getProductSizesData().forEach(size -> {
                    int available = calculateAvailableStock(size);
                    log.info("  尺码: {}, 可用库存: {}", size.getSizePanelName(), available);
                });
            });
        }
    }
    
    /**
     * 打印库存汇总信息
     */
    private void printStockSummary(List<ProductResponse.Product> products) {
        int totalProducts = products.size();
        int totalStock = products.stream()
            .mapToInt(this::calculateTotalStock)
            .sum();
            
        // 统计库存区间
        Map<String, Long> stockRanges = products.stream()
            .collect(Collectors.groupingBy(
                product -> {
                    int stock = calculateTotalStock(product);
                    if (stock == 0) return "无库存";
                    if (stock <= 10) return "1-10";
                    if (stock <= 50) return "11-50";
                    if (stock <= 100) return "51-100";
                    if (stock <= 500) return "101-500";
                    return "500+";
                },
                Collectors.counting()
            ));
            
        log.info("库存汇总信息：");
        log.info("商品总数: {}", totalProducts);
        log.info("总库存数: {}", totalStock);
        log.info("库存分布：");
        stockRanges.forEach((range, count) -> 
            log.info("  {}: {} 个商品", range, count));
    }
} 