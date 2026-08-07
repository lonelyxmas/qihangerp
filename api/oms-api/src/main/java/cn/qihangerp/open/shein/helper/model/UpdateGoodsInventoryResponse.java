package cn.qihangerp.open.shein.helper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateGoodsInventoryResponse {
    @JsonProperty("failedList")
    private List<FailedInfo> failedList;

    @JsonProperty("successList")
    private List<SuccessInfo> successList;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FailedInfo {
        @JsonProperty("code")
        private String code;

        @JsonProperty("reason")
        private String reason;

        @JsonProperty("skuCode")
        private String skuCode;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SuccessInfo {

        @JsonProperty("skuCode")
        private String skuCode;
    }


} 