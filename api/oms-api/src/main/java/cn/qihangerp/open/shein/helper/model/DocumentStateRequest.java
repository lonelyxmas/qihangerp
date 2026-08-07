package cn.qihangerp.open.shein.helper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DocumentStateRequest extends BaseRequest {
    @JsonProperty("spuList")
    private List<SpuInfo> spuList;
    
    @Data
    public static class SpuInfo {
        @JsonProperty("spuName")
        private String spuName;
        
        @JsonProperty("version")
        private String version;
    }
} 