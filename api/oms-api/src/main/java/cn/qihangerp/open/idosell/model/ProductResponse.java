package cn.qihangerp.open.idosell.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

/**
 * IdoSell API 商品列表响应实体类
 * 用于封装从IdoSell API获取的商品列表数据
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResponse {
    /** 当前页码，从0开始 */
    @JsonProperty("resultsPage")
    private Integer resultsPage;
    
    /** 每页显示的记录数量 */
    @JsonProperty("resultsLimit") 
    private Integer resultsLimit;
    
    /** 总页数 */
    @JsonProperty("resultsNumberPage")
    private Integer resultsNumberPage;
    
    /** 总记录数 */
    @JsonProperty("resultsNumberAll")
    private Integer resultsNumberAll;
    
    /** 商品列表数据 */
    @JsonProperty("results")
    private List<Product> results;

    /**
     * 商品详细信息实体类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Product {
        /** 商品ID */
        @JsonProperty("productId")
        private Integer productId;
        
        /** 商品显示编码 */
        @JsonProperty("productDisplayedCode")
        private String productDisplayedCode;
        
        /** 商品多语言描述数据 */
        @JsonProperty("productDescriptionsLangData")
        private List<ProductDescription> productDescriptionsLangData;
        
        /** 商品是否已删除 y/n */
        @JsonProperty("productIsDeleted")
        private String productIsDeleted;

        @JsonProperty("delivererId")
        private Integer delivererId;

        @JsonProperty("delivererName")
        private String delivererName;

        @JsonProperty("productVatFree")
        private String productVatFree;
        @JsonProperty("productIsVisible")
        private String productIsVisible;

        @JsonProperty("productAddingTime")
        private String productAddingTime;

        @JsonProperty("productQuantityChangedTime")
        private String productQuantityChangedTime;

        @JsonProperty("currencyId")
        private String currencyId;
        /** 商品备注 */
        @JsonProperty("productNote")
        private String productNote;
        
        /** 商品税收代码 */
        @JsonProperty("productTaxCode")
        private String productTaxCode;
        
        /** 制造商ID */
        @JsonProperty("producerId")
        private Long producerId;
        
        /** 制造商名称 */
        @JsonProperty("producerName")
        private String producerName;
        
        /** 商品分类ID */
        @JsonProperty("categoryId")
        private Long categoryId;
        
        /** 商品分类名称 */
        @JsonProperty("categoryName")
        private String categoryName;
        
        /** 零售价格 */
        @JsonProperty("productRetailPrice")
        private Double productRetailPrice;
        
        /** 批发价格 */
        @JsonProperty("productWholesalePrice")
        private Double productWholesalePrice;
        
        /** 增值税率 */
        @JsonProperty("productVat")
        private Integer productVat;
        
        /** 商品图片列表 */
        @JsonProperty("productImages")
        private List<ProductImage> productImages;
        
        /** 商品参数列表 */
        @JsonProperty("productParameters")
        private List<ProductParameter> productParameters;
        
        /** 商品拍卖描述数据 */
        @JsonProperty("productAuctionDescriptionsData")
        private List<ProductAuctionDescription> productAuctionDescriptionsData;

        /** 商品库存数据 */
        @JsonProperty("productStocksData")
        private ProductStocksData productStocksData;
    }
    
    /**
     * 商品多语言描述信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductDescription {
        /** 语言ID，如: pol-波兰语, eng-英语 */
        @JsonProperty("langId")
        private String langId;
        
        /** 商品名称 */
        @JsonProperty("productName")
        private String productName;
        
        /** 商品拍卖名称 */
        @JsonProperty("productAuctionName")
        private String productAuctionName;
        
        /** 商品简短描述 */
        @JsonProperty("productDescription")
        private String productDescription;
        
        /** 商品详细描述 */
        @JsonProperty("productLongDescription")
        private String productLongDescription;
        
        /** 商品Meta标题 */
        @JsonProperty("productMetaTitle")
        private String productMetaTitle;
        
        /** 商品Meta关键词 */
        @JsonProperty("productMetaKeywords")
        private String productMetaKeywords;

        /** 商品价格比较站点名称 */
        @JsonProperty("productPriceComparisonSitesName")
        private String productPriceComparisonSitesName;
    }
    
    /**
     * 商品图片信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductImage {
        /** 大图URL */
        @JsonProperty("productImageLargeUrl")
        private String productImageLargeUrl;
        
        /** 中图URL */
        @JsonProperty("productImageMediumUrl")
        private String productImageMediumUrl;
        
        /** 小图URL */
        @JsonProperty("productImageSmallUrl")
        private String productImageSmallUrl;
        
        /** 图片ID */
        @JsonProperty("productImageId")
        private String productImageId;
        
        /** 图片宽度(像素) */
        @JsonProperty("productImageWidth")
        private Integer productImageWidth;
        
        /** 图片高度(像素) */
        @JsonProperty("productImageHeight")
        private Integer productImageHeight;
        
        /** 图片大小(KB) */
        @JsonProperty("productImageSize")
        private Double productImageSize;
        
        /** 图片显示优先级 */
        @JsonProperty("productImagePriority")
        private Integer productImagePriority;
    }
    
    /**
     * 商品参数信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductParameter {
        /** 参数ID */
        @JsonProperty("parameterId")
        private Integer parameterId;
        
        /** 参数类型 */
        @JsonProperty("parameterType")
        private String parameterType;
        
        /** 参数多语言描述 */
        @JsonProperty("parameterDescriptionsLangData")
        private List<ParameterDescription> parameterDescriptionsLangData;
        
        /** 参数值列表 */
        @JsonProperty("parameterValues")
        private List<ParameterValue> parameterValues;
    }
    
    /**
     * 参数描述信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ParameterDescription {
        /** 语言ID */
        @JsonProperty("langId")
        private String langId;
        
        /** 参数名称 */
        @JsonProperty("parameterName")
        private String parameterName;
        
        /** 参数描述 */
        @JsonProperty("parameterDescription")
        private String parameterDescription;
    }
    
    /**
     * 参数值信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ParameterValue {
        /** 参数值ID */
        @JsonProperty("parameterValueId")
        private Integer parameterValueId;
        
        /** 参数值的多语言描述 */
        @JsonProperty("parameterValueDescriptionsLangData")
        private List<ParameterValueDescription> parameterValueDescriptionsLangData;
    }
    
    /**
     * 参数值描述信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ParameterValueDescription {
        /** 语言ID */
        @JsonProperty("langId")
        private String langId;
        
        /** 参数值名称 */
        @JsonProperty("parameterValueName")
        private String parameterValueName;
        
        /** 参数值描述 */
        @JsonProperty("parameterValueDescription")
        private String parameterValueDescription;
    }
    
    /**
     * 商品拍卖描述信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductAuctionDescription {
        /** 商品拍卖ID */
        @JsonProperty("productAuctionId")
        private Integer productAuctionId;
        
        /** 商品拍卖站点ID */
        @JsonProperty("productAuctionSiteId")
        private Integer productAuctionSiteId;
        
        /** 商品拍卖名称 */
        @JsonProperty("productAuctionName")
        private String productAuctionName;
        
        /** 商品拍卖附加名称 */
        @JsonProperty("productAuctionAdditionalName")
        private String productAuctionAdditionalName;
        
        /** 商品拍卖描述 */
        @JsonProperty("productAuctionDescription")
        private String productAuctionDescription;
    }

    /**
     * 商品库存数据
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductStocksData {
        /** 商品尺码库存数量信息 */
        @JsonProperty("productStocksQuantities")
        private List<ProductStockQuantity> productStocksQuantities;
    }

    /**
     * 商品库存数量信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductStockQuantity {
        /** 仓库ID */
        @JsonProperty("stockId")
        private Integer stockId;

        /** 商品尺码数据 */
        @JsonProperty("productSizesData")
        private List<ProductSizeData> productSizesData;
    }

    /**
     * 商品尺码数据
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductSizeData {
        /** 尺码ID */
        @JsonProperty("sizeId")
        private String sizeId;

        /** 尺码名称 */
        @JsonProperty("sizePanelName")
        private String sizePanelName;

        /** 尺码外部编码 */
        @JsonProperty("productSizeCodeExternal")
        private String productSizeCodeExternal;

        /** 尺码库存数量 */
        @JsonProperty("productSizeQuantity")
        private Integer productSizeQuantity;

        /** 尺码预留信息 */
        @JsonProperty("productSizeReservations")
        private ProductSizeReservations productSizeReservations;
    }

    /**
     * 商品尺码预留信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductSizeReservations {
        /** 临时预留数量 */
        @JsonProperty("productSizeReservationAdhoc")
        private Integer productSizeReservationAdhoc;

        /** 拍卖预留数量 */
        @JsonProperty("productSizeReservationAuction")
        private Integer productSizeReservationAuction;

        /** 客户预留数量 */
        @JsonProperty("productSizeReservationClient")
        private Integer productSizeReservationClient;

        /** 订单预留数量 */
        @JsonProperty("productSizeReservationOrder")
        private Integer productSizeReservationOrder;

        /** 零售预留数量 */
        @JsonProperty("productSizeReservationRetail")
        private Integer productSizeReservationRetail;

        /** 批发预留数量 */
        @JsonProperty("productSizeReservationWholesale")
        private Integer productSizeReservationWholesale;
    }
} 