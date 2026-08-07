package cn.qihangerp.open.shein.helper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDetailResponse {
    @JsonProperty("info")
    private List<OrderDetailInfo> info;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderDetailInfo {
        @JsonProperty("orderNo")
        private String orderNo;

        @JsonProperty("orderType")
        private Integer orderType;

        @JsonProperty("orderStatus")
        private String orderStatus;

        @JsonProperty("orderGoodsInfoList")
        private List<OrderGoodsDetailInfo> orderGoodsInfoList;

        @JsonProperty("orderTime")
        private String orderTime;

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderGoodsDetailInfo {
        @JsonProperty("goodsId")
        private Long goodsId;

        @JsonProperty("skuCode")
        private String skuCode;

        @JsonProperty("skuCode")
        private String spuName;

        @JsonProperty("newGoodsStatus")
        private String newGoodsStatus;

        @JsonProperty("saleCurrency")
        private String saleCurrency;

        @JsonProperty("sellerCurrencyPrice")
        private BigDecimal sellerCurrencyPrice;

    }


} 