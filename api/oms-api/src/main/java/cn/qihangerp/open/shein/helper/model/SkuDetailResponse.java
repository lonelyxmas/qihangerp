package cn.qihangerp.open.shein.helper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SkuDetailResponse  {
    @JsonProperty("info")
    private List<SkuInfo> info;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SkuInfo {
        @JsonProperty("skuCode")
        private String skuCode;

        @JsonProperty("spuName")
        private String spuName;

        @JsonProperty("skcName")
        private String skcName;

        @JsonProperty("productName")
        private ProductName productName;

        @JsonProperty("productDesc")
        private ProductDesc productDesc;

        @JsonProperty("productNumber")
        private String productNumber;

        @JsonProperty("goodsInventory")
        private InventoryInfo goodsInventory;

        @JsonProperty("warehouseInventoryList")
        private List<WarehouseInventory> warehouseInventoryList;

        @JsonProperty("attributeLists")
        private List<AttributeInfo> attributeLists;

        @JsonProperty("skuAttributeLists")
        private List<AttributeInfo> skuAttributeLists;

        @JsonProperty("saleAttribute")
        private SaleAttribute saleAttribute;

        @JsonProperty("currentPrices")
        private List<PriceInfo> currentPrices;

        @JsonProperty("categoryName")
        private String categoryName;

        @JsonProperty("categoryId")
        private String categoryId;

        @JsonProperty("imageList")
        private List<ImageInfo> imageList;

        @JsonProperty("skuDimensionsInfo")
        private DimensionsInfo skuDimensionsInfo;

        @JsonProperty("shelfDetails")
        private List<ShelfDetail> shelfDetails;

        @JsonProperty("stockMode")
        private String stockMode;

        @JsonProperty("isRecycled")
        private String isRecycled;

        @JsonProperty("productTypeId")
        private String productTypeId;

        @JsonProperty("brandCode")
        private String brandCode;

        @JsonProperty("stopPurchase")
        private String stopPurchase;

        @JsonProperty("mallStateList")
        private List<MallState> mallStateList;

        @JsonProperty("siteDetailImageInfoList")
        private List<SiteDetailImageInfo> siteDetailImageInfoList;

        @JsonProperty("siteList")
        private List<SiteInfo> siteList;

        @JsonProperty("proofOfStockList")
        private List<ProofOfStock> proofOfStockList;

        @JsonProperty("competingProductLink")
        private String competingProductLink;

        @JsonProperty("sampleInfo")
        private SampleInfo sampleInfo;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductName {
        @JsonProperty("productName")
        private String productName;

        @JsonProperty("language")
        private String language;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductDesc {
        @JsonProperty("productDesc")
        private String productDesc;

        @JsonProperty("language")
        private String language;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InventoryInfo {
        @JsonProperty("inventoryQuantity")
        private Integer inventoryQuantity;

        @JsonProperty("lockedQuantity")
        private Integer lockedQuantity;

        @JsonProperty("usableInventory")
        private Integer usableInventory;

        @JsonProperty("tempLockQuantity")
        private Integer tempLockQuantity;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WarehouseInventory {
        @JsonProperty("warehouseCode")
        private String warehouseCode;

        @JsonProperty("warehouseType")
        private String warehouseType;

        @JsonProperty("inventoryQuantity")
        private Integer inventoryQuantity;

        @JsonProperty("lockedQuantity")
        private Integer lockedQuantity;

        @JsonProperty("usableInventory")
        private Integer usableInventory;

        @JsonProperty("tempLockQuantity")
        private Integer tempLockQuantity;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AttributeInfo {
        @JsonProperty("attributeId")
        private Long attributeId;

        @JsonProperty("attributeValueMulti")
        private AttributeValueMulti attributeValueMulti;

        @JsonProperty("attributeValueId")
        private String attributeValueId;

        @JsonProperty("attributeMulti")
        private AttributeMulti attributeMulti;

        @JsonProperty("attributeType")
        private String attributeType;

        @JsonProperty("attributeAdditionList")
        private List<AttributeAddition> attributeAdditionList;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AttributeValueMulti {
        @JsonProperty("attributeValueMulti")
        private String attributeValueMulti;

        @JsonProperty("language")
        private String language;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AttributeMulti {
        @JsonProperty("attributeMulti")
        private String attributeMulti;

        @JsonProperty("language")
        private String language;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AttributeAddition {
        @JsonProperty("additionAttrValueMulti")
        private AdditionAttrValueMulti additionAttrValueMulti;

        @JsonProperty("additionValue")
        private String additionValue;

        @JsonProperty("additionAttrValueId")
        private String additionAttrValueId;

        @JsonProperty("additionAttrMulti")
        private AdditionAttrMulti additionAttrMulti;

        @JsonProperty("additionAttrId")
        private String additionAttrId;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AdditionAttrValueMulti {
        @JsonProperty("additionAttrValueMulti")
        private String additionAttrValueMulti;

        @JsonProperty("language")
        private String language;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AdditionAttrMulti {
        @JsonProperty("additionAttrMulti")
        private String additionAttrMulti;

        @JsonProperty("language")
        private String language;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SaleAttribute {
        // Add fields as needed
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PriceInfo {
        @JsonProperty("shopPrice")
        private BigDecimal shopPrice;

        @JsonProperty("specialPrice")
        private BigDecimal specialPrice;

        @JsonProperty("salePrice")
        private BigDecimal salePrice;

        @JsonProperty("currency")
        private String currency;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageInfo {
        @JsonProperty("imageType")
        private String imageType;

        @JsonProperty("sort")
        private Integer sort;
        @JsonProperty("imageSort")
        private Integer imageSort;
        @JsonProperty("imageMediumUrl")
        private String imageMediumUrl;

        @JsonProperty("imageSmallUrl")
        private String imageSmallUrl;

        @JsonProperty("imageUrl")
        private String imageUrl;

        @JsonProperty("imageItemId")
        private Long imageItemId;

        @JsonProperty("groupCode")
        private String groupCode;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DimensionsInfo {
        @JsonProperty("length")
        private String length;

        @JsonProperty("width")
        private String width;

        @JsonProperty("height")
        private String height;

        @JsonProperty("weight")
        private String weight;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ShelfDetail {
        @JsonProperty("isOnShelf")
        private Boolean isOnShelf;

        @JsonProperty("lastUpdateTime")
        private String lastUpdateTime;

        @JsonProperty("site")
        private String site;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MallState {
        @JsonProperty("mallState")
        private String mallState;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SiteDetailImageInfo {
        @JsonProperty("imageGroupCode")
        private String imageGroupCode;

        @JsonProperty("imageInfoList")
        private List<ImageInfo> imageInfoList;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SiteInfo {
        @JsonProperty("channel")
        private String channel;

        @JsonProperty("mainSite")
        private String mainSite;

        @JsonProperty("site")
        private String site;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProofOfStock {
        @JsonProperty("fileName")
        private String fileName;

        @JsonProperty("type")
        private Integer type;

        @JsonProperty("url")
        private String url;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SampleInfo {
        @JsonProperty("reserveSampleFlag")
        private String reserveSampleFlag;

        @JsonProperty("spotFlag")
        private String spotFlag;

        @JsonProperty("sampleJudgeType")
        private String sampleJudgeType;

        @JsonProperty("sampleCode")
        private String sampleCode;
    }
} 