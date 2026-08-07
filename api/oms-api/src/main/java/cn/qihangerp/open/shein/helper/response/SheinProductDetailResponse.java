package cn.qihangerp.open.shein.helper.response;

import cn.qihangerp.open.shein.helper.model.SkuDetailResponse;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Data
public class SheinProductDetailResponse {

    @JsonProperty("code")
    private String code;
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("info")
    private InfoDTO info;
    @JsonProperty("bbl")
    private Object bbl;
    @JsonProperty("traceId")
    private String traceId;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @NoArgsConstructor
    @Data
    public static class InfoDTO {
        @JsonProperty("spuName")
        private String spuName;
        @JsonProperty("categoryId")
        private Long categoryId;
        @JsonProperty("productTypeId")
        private Long productTypeId;
        @JsonProperty("brandCode")
        private String brandCode;
        @JsonProperty("supplierCode")
        private String supplierCode;
        @JsonProperty("productMultiNameList")
        private List<ProductMultiNameListDTO> productMultiNameList;
        @JsonProperty("productMultiDescList")
        private List<ProductMultiDescListDTO> productMultiDescList;
        @JsonProperty("productAttributeInfoList")
        private List<ProductAttributeInfoListDTO> productAttributeInfoList;
        @JsonProperty("dimensionAttributeInfoList")
        private List<?> dimensionAttributeInfoList;
        @JsonProperty("spuImageInfoList")
        private List<SpuImageInfoList> spuImageInfoList;
        @JsonProperty("skcInfoList")
        private List<SkcInfoListDTO> skcInfoList;

        @JsonIgnoreProperties(ignoreUnknown = true)
        @NoArgsConstructor
        @Data
        public static class SpuImageInfoList {
            @JsonProperty("groupCode")
            private String groupCode;
            @JsonProperty("imageItemId")
            private Long imageItemId;
            @JsonProperty("imageMediumUrl")
            private String imageMediumUrl;
            @JsonProperty("imageSmallUrl")
            private String imageSmallUrl;
            @JsonProperty("imageType")
            private String imageType;
            @JsonProperty("imageUrl")
            private String imageUrl;
            @JsonProperty("sort")
            private String sort;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        @NoArgsConstructor
        @Data
        public static class ProductMultiNameListDTO {
            @JsonProperty("productName")
            private String productName;
            @JsonProperty("language")
            private String language;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        @NoArgsConstructor
        @Data
        public static class ProductMultiDescListDTO {
            @JsonProperty("productDesc")
            private String productDesc;
            @JsonProperty("language")
            private String language;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        @NoArgsConstructor
        @Data
        public static class ProductAttributeInfoListDTO {
            @JsonProperty("attributeId")
            private Integer attributeId;
            @JsonProperty("attributeMultiList")
            private List<AttributeMultiListDTO> attributeMultiList;
            @JsonProperty("attributeValueId")
            private Integer attributeValueId;
            @JsonProperty("attributeValueMultiList")
            private List<AttributeValueMultiListDTO> attributeValueMultiList;
            @JsonProperty("attributeValue")
            private Object attributeValue;

            @JsonIgnoreProperties(ignoreUnknown = true)
            @NoArgsConstructor
            @Data
            public static class AttributeMultiListDTO {
                @JsonProperty("attributeName")
                private String attributeName;
                @JsonProperty("language")
                private String language;
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            @NoArgsConstructor
            @Data
            public static class AttributeValueMultiListDTO {
                @JsonProperty("attributeValueName")
                private String attributeValueName;
                @JsonProperty("language")
                private String language;
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        @NoArgsConstructor
        @Data
        public static class SkcInfoListDTO {
            @JsonProperty("skcName")
            private String skcName;
            @JsonProperty("supplierCode")
            private String supplierCode;
            @JsonProperty("sampleInfo")
            private SampleInfoDTO sampleInfo;
            @JsonProperty("productMultiNameList")
            private List<ProductMultiNameListDTO> productMultiNameList;
            @JsonProperty("attributeId")
            private Integer attributeId;
            @JsonProperty("attributeMultiList")
            private List<AttributeMultiListDTO> attributeMultiList;
            @JsonProperty("attributeValueId")
            private Integer attributeValueId;
            @JsonProperty("attributeValueMultiList")
            private List<AttributeValueMultiListDTO> attributeValueMultiList;
            @JsonProperty("skuInfoList")
            private List<SkuInfoListDTO> skuInfoList;
            @JsonProperty("shelfStatusInfoList")
            private List<ShelfStatusInfoListDTO> shelfStatusInfoList;
            @JsonProperty("skcImageInfoList")
            private List<SkcImageInfoListDTO> skcImageInfoList;
            @JsonProperty("siteDetailImageInfoList")
            private List<SkuDetailResponse.SiteDetailImageInfo> siteDetailImageInfoList;
            @JsonProperty("proofOfStockInfoList")
            private List<?> proofOfStockInfoList;
            @JsonProperty("srpPriceInfo")
            private JSONObject srpPriceInfo;

            @JsonIgnoreProperties(ignoreUnknown = true)
            @NoArgsConstructor
            @Data
            public static class SampleInfoDTO {
                @JsonProperty("sampleCode")
                private String sampleCode;
                @JsonProperty("reserveSampleFlag")
                private Integer reserveSampleFlag;
                @JsonProperty("spotFlag")
                private Integer spotFlag;
                @JsonProperty("sampleJudgeType")
                private Integer sampleJudgeType;
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            @NoArgsConstructor
            @Data
            public static class ProductMultiNameListDTO {
                @JsonProperty("productName")
                private String productName;
                @JsonProperty("language")
                private String language;
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            @NoArgsConstructor
            @Data
            public static class AttributeMultiListDTO {
                @JsonProperty("attributeName")
                private String attributeName;
                @JsonProperty("language")
                private String language;
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            @NoArgsConstructor
            @Data
            public static class AttributeValueMultiListDTO {
                @JsonProperty("attributeValueName")
                private String attributeValueName;
                @JsonProperty("language")
                private String language;
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            @NoArgsConstructor
            @Data
            public static class SkuInfoListDTO {
                @JsonProperty("skuCode")
                private String skuCode;
                @JsonProperty("supplierSku")
                private String supplierSku;
                @JsonProperty("length")
                private String length;
                @JsonProperty("width")
                private String width;
                @JsonProperty("height")
                private String height;
                @JsonProperty("weight")
                private Integer weight;
                @JsonProperty("mallState")
                private Integer mallState;
                @JsonProperty("stopPurchase")
                private Integer stopPurchase;
                @JsonProperty("saleAttributeList")
                private List<SaleAttributeListDTO> saleAttributeList;
                @JsonProperty("priceInfoList")
                private List<priceInfo> priceInfoList;
                @JsonProperty("costInfoList")
                private List<CostInfoListDTO> costInfoList;
                @JsonProperty("skuImageInfoList")
                private List<skuImageInfo> skuImageInfoList;

                @JsonIgnoreProperties(ignoreUnknown = true)
                @NoArgsConstructor
                @Data
                public static class skuImageInfo {
                    @JsonProperty("groupCode")
                    private String groupCode;
                    @JsonProperty("imageItemId")
                    private Long imageItemId;
                    @JsonProperty("imageMediumUrl")
                    private String imageMediumUrl;
                    @JsonProperty("imageSmallUrl")
                    private String imageSmallUrl;
                    @JsonProperty("imageType")
                    private String imageType;
                    @JsonProperty("imageUrl")
                    private String imageUrl;
                    @JsonProperty("sort")
                    private Integer sort;
                }

                @JsonIgnoreProperties(ignoreUnknown = true)
                @NoArgsConstructor
                @Data
                public static class priceInfo {
                    @JsonProperty("basePrice")
                    private Double basePrice;
                    @JsonProperty("currency")
                    private String currency;
                    @JsonProperty("site")
                    private String site;
                    @JsonProperty("specialPrice")
                    private Double specialPrice;
                }

                @JsonIgnoreProperties(ignoreUnknown = true)
                @NoArgsConstructor
                @Data
                public static class SaleAttributeListDTO {
                    @JsonProperty("attributeId")
                    private Integer attributeId;
                    @JsonProperty("attributeValueId")
                    private Integer attributeValueId;
                    @JsonProperty("attributeValueMultiList")
                    private List<AttributeValueMultiListDTO> attributeValueMultiList;

                    @JsonIgnoreProperties(ignoreUnknown = true)
                    @NoArgsConstructor
                    @Data
                    public static class AttributeValueMultiListDTO {
                        @JsonProperty("attributeValueName")
                        private String attributeValueName;
                        @JsonProperty("language")
                        private String language;
                    }
                }

                @JsonIgnoreProperties(ignoreUnknown = true)
                @NoArgsConstructor
                @Data
                public static class CostInfoListDTO {
                    @JsonProperty("currency")
                    private String currency;
                    @JsonProperty("costPrice")
                    private Double costPrice;
                }
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            @NoArgsConstructor
            @Data
            public static class ShelfStatusInfoListDTO {
                @JsonProperty("siteAbbr")
                private String siteAbbr;
                @JsonProperty("shelfStatus")
                private Integer shelfStatus;
                @JsonProperty("lastShelfTime")
                private String lastShelfTime;
                @JsonProperty("firstShelfTime")
                private String firstShelfTime;
                @JsonProperty("lastUpdateTime")
                private String lastUpdateTime;
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            @NoArgsConstructor
            @Data
            public static class SkcImageInfoListDTO {
                @JsonProperty("groupCode")
                private String groupCode;
                @JsonProperty("imageItemId")
                private Integer imageItemId;
                @JsonProperty("imageType")
                private String imageType;
                @JsonProperty("imageMediumUrl")
                private String imageMediumUrl;
                @JsonProperty("imageSmallUrl")
                private String imageSmallUrl;
                @JsonProperty("imageUrl")
                private String imageUrl;
                @JsonProperty("sort")
                private Integer sort;
            }
        }
    }
}
