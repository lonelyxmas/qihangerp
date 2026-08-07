package cn.qihangerp.open.shein.helper.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SheinPurchaseOrderResponse {
    @JsonProperty("code")
    private Integer code;
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("info")
    private SheinPurchaseOrderResponse.ListInfo info;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ListInfo {
        @JsonProperty("count")
        private Integer count;
        @JsonProperty("pageNo")
        private Integer pageNo;
        @JsonProperty("pageSize")
        private Integer pageSize;
        @JsonProperty("list")
        private List<SheinPurchaseOrder> orderList;
    }
}
