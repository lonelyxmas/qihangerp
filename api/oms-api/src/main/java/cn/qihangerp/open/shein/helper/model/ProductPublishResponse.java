package cn.qihangerp.open.shein.helper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductPublishResponse {
    private String code;
    private String msg;
    private ResponseInfo info;
    private Object bbl;
    private String traceId;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseInfo {
        @JsonProperty("pre_valid_result")
        private List<ValidResult> preValidResult;
        
        @JsonProperty("mcc_valid_result")
        private List<MccValidResult> mccValidResult;
        
        @JsonProperty("spu_name")
        private String spuName;
        
        @JsonProperty("skc_list")
        private List<SkcInfo> skcList;
        
        private Boolean success;
        private String version;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ValidResult {
        private String form;
        private List<String> messages;
        private String module;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MccValidResult {
        private String message;
        private Integer type;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SkcInfo {
        @JsonProperty("skc_name")
        private String skcName;

        @JsonProperty("sku_list")
        private List<SkuInfo> skuList;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SkuInfo {
        @JsonProperty("sku_code")
        private String skuCode;
        
        @JsonProperty("supplier_sku")
        private String supplierSku;
    }
} 