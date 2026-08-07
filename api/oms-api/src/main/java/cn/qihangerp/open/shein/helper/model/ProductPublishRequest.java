package cn.qihangerp.open.shein.helper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductPublishRequest extends BaseRequest {
    @JsonProperty("product_type_id")
    private Long productTypeId;
    
    @JsonProperty("source_system")
    private String sourceSystem;

    @JsonProperty("spu_name")
    private String spuName;
    
    @JsonProperty("category_id")
    private Long categoryId;
    
    @JsonProperty("brand_id")
    private Long brandId;
    
    @JsonProperty("supplier_code")
    private String supplierCode;
    
    @JsonProperty("suit_flag")
    private Integer suitFlag;

    @JsonProperty("site_list")
    private List<SiteInfo> siteList;
    
    @JsonProperty("multi_language_name_list")
    private List<MultiLanguageInfo> multiLanguageNameList;
    
    @JsonProperty("multi_language_desc_list")
    private List<MultiLanguageInfo> multiLanguageDescList;
    
    @JsonProperty("product_attribute_list")
    private List<ProductAttribute> productAttribute;
    
    @JsonProperty("skc_list")
    private List<SkcInfo> skcInfo;
    
    @Data
    public static class MultiLanguageInfo {
        private String language;
        private String name;
        private String description;
    }

    @Data
    public static class SiteInfo{
        @JsonProperty("main_site")
        private String mainSite;

        @JsonProperty("sub_site_list")
        private List<String> subSiteList;
    }
    
    @Data
    public static class ProductAttribute {
        @JsonProperty("attribute_id")
        private Long attributeId;
        
        @JsonProperty("attribute_value_id")
        private Long attributeValueId;
        
        @JsonProperty("attribute_value")
        private String attributeValue;
        
        @JsonProperty("attribute_extra_value")
        private String attributeExtraValue;
    }
    
    @Data
    public static class SkcInfo {
        @JsonProperty("supplier_code")
        private String supplierCode;

        @JsonProperty("skc_name")
        private String skcCode;

        @JsonProperty("sale_attribute")
        private SaleAttribute saleAttribute;
        
        @JsonProperty("image_info")
        private ImageInfo imageInfo;
        
        @JsonProperty("sku_list")
        private List<SkuInfo> skuInfo;

        @JsonProperty("skc_title")
        private String skcTitle;
    }
    
    @Data
    public static class ImageInfo {
        @JsonProperty("image_info_list")
        private List<ImageInfoItem> imageInfoList;
    }
    
    @Data
    public static class ImageInfoItem {
        @JsonProperty("image_type")
        private Integer imageType;
        
        @JsonProperty("image_sort")
        private Integer imageSort;
        
        @JsonProperty("image_url")
        private String imageUrl;
    }
    
    @Data
    public static class SkuInfo {
        @JsonProperty("supplier_sku")
        private String supplierSku;
        
        @JsonProperty("mall_state")
        private Integer mallState;
        
        @JsonProperty("stop_purchase")
        private Integer stopPurchase;
        
        private String height;
        private String length;
        private String width;
        private String weight;
        
        @JsonProperty("price_info_list")
        private List<PriceInfo> priceInfoList;

        @JsonProperty("cost_info")
        private CostInfo costInfo;

        @JsonProperty("sku_code")
        private String skuCode;
        
        @JsonProperty("stock_info_list")
        private List<StockInfo> stockInfoList;

        @JsonProperty("sale_attribute_list")
        private List<SaleAttribute> saleAttributeList;
    }
    
    @Data
    public static class PriceInfo {
        @JsonProperty("base_price")
        private BigDecimal basePrice;
        
        private String currency;
        
        @JsonProperty("sub_site")
        private String subSite;
    }
    
    @Data
    public static class StockInfo {
        @JsonProperty("inventory_num")
        private Integer inventoryNum;
    }

    @Data
    public static class CostInfo {
        @JsonProperty("cost_price")
        private BigDecimal costPrice;

        @JsonProperty("currency")
        private String currency;
    }
    
    @Data
    public static class SaleAttribute {
        @JsonProperty("attribute_id")
        private Long attributeId;
        
        @JsonProperty("attribute_value_id")
        private Long attributeValueId;
    }
} 