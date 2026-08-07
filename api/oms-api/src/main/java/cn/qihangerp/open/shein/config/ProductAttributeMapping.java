package cn.qihangerp.open.shein.config;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProductAttributeMapping {
    @JSONField(name = "categoryMapping")
    private Map<String, CategoryMapping> categoryMapping;

    @JSONField(name = "currency")
    private String currency;

    @JSONField(name = "mainSite")
    private String mainSite;

    @JSONField(name = "subSiteList")
    private List<String> subSiteList;

    @JSONField(name = "productAttributeList")
    private List<ProductAttributeValue> productAttributeList;

    @JSONField(name = "saleSkcAttributeMapping")
    private AttributeMapping saleSkcAttributeMapping;

    @JSONField(name = "saleSkuAttributeMapping")
    private AttributeMapping saleSkuAttributeMapping;


    @Data
    public static class ProductAttributeValue {
        @JSONField(name = "productAttributeId")
        private String productAttributeId;

        @JSONField(name = "productAttributeName")
        private String productAttributeName;

        @JSONField(name = "productAttributeValueId")
        private String productAttributeValueId;

        @JSONField(name = "productAttributeValueName")
        private String productAttributeValueName;
    }

    @Data
    public static class CategoryMapping {
        @JSONField(name = "shenCategoryId")
        private String shenCategoryId;

        @JSONField(name = "sheinProductId")
        private String sheinProductId;
    }

    @Data
    public static class AttributeMapping {
        @JSONField(name = "attributeId")
        private String attributeId;

        @JSONField(name = "attributeName")
        private String attributeName;

        @JSONField(name = "mapping")
        private Map<String, AttributeValue> mapping;
    }

    @Data
    public static class AttributeValue {
        @JSONField(name = "attributeValueId")
        private String attributeValueId;

        @JSONField(name = "attributeValueName")
        private String attributeValueName;
    }
}
