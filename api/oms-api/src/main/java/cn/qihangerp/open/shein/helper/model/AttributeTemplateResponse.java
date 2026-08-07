package cn.qihangerp.open.shein.helper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributeTemplateResponse {
    private String code;
    private String msg;
    private Info info;
    private Object bbl;
    private String traceId;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Info {
        private List<AttributeData> data;
        private Meta meta;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AttributeData {
        @JsonProperty("product_type_id")
        private Long productTypeId;
        
        @JsonProperty("attribute_infos")
        private List<AttributeInfo> attributeInfos;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AttributeInfo {
        @JsonProperty("attribute_id")
        private Long attributeId;
        
        @JsonProperty("attribute_name")
        private String attributeName;
        
        @JsonProperty("attribute_type")
        private Integer attributeType;
        
        @JsonProperty("attribute_label")
        private Integer attributeLabel;
        
        @JsonProperty("attribute_status")
        private Integer attributeStatus;
        
        @JsonProperty("attribute_mode")
        private Integer attributeMode;
        
        @JsonProperty("attribute_is_show")
        private Integer attributeIsShow;
        
        @JsonProperty("attribute_value_info_list")
        private List<AttributeValueInfo> attributeValueInfoList;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AttributeValueInfo {
        @JsonProperty("attribute_value")
        private String attributeValue;
        
        @JsonProperty("attribute_value_id")
        private Long attributeValueId;
        
        @JsonProperty("is_show")
        private Integer isShow;
        
        @JsonProperty("is_custom_attribute_value")
        private Boolean isCustomAttributeValue;
        
        @JsonProperty("supplier_id")
        private Long supplierId;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meta {
        private Integer count;
    }
} 