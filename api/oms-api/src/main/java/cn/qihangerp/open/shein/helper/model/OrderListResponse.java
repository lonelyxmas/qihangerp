package cn.qihangerp.open.shein.helper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderListResponse {
    @JsonProperty("info")
    private OrderListInfo info;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderListInfo {
        @JsonProperty("count")
        private Integer count;

        private List<OrderListDo> orderList;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderListDo {
        @JsonProperty("orderNo")
        private String orderNo;

        @JsonProperty("orderStatus")
        private String orderStatus;

        @JsonProperty("orderCreateTime")
        private String orderCreateTime;

        @JsonProperty("orderUpdateTime")
        private String orderUpdateTime;
    }
} 