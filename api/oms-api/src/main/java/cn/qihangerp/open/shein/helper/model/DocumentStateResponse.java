package cn.qihangerp.open.shein.helper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentStateResponse {
    private String code;
    private String msg;
    private Info info;
    private Object bbl;
    private String traceId;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Info {
        private List<SpuStateInfo> data;
        private Meta meta;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SpuStateInfo {
        @JsonProperty("spuName")
        private String spuName;
        
        @JsonProperty("version")
        private String version;
        
        @JsonProperty("skcList")
        private List<SkcStateInfo> skcList;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SkcStateInfo {
        @JsonProperty("skcName")
        private String skcName;
        
        @JsonProperty("documentSn")
        private String documentSn;
        
        @JsonProperty("documentState")
        private Integer documentState;
        
        @JsonProperty("failedReason")
        private List<FailedReason> failedReason;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FailedReason {
        private String language;
        private String content;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meta {
        private Integer count;
        private Object customObj;
    }
} 