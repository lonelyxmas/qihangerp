package cn.qihangerp.open.shein.helper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductListResponse {
    private String code;
    private String msg;
    private Info info;
    private String traceId;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Info {
        private List<ProductInfo> data;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductInfo {
        @JsonProperty("skcName")
        private String skcName;
        
        @JsonProperty("skuCodeList")
        private List<String> skuCodeList;
        
        @JsonProperty("spuName")
        private String spuName;
    }
} 