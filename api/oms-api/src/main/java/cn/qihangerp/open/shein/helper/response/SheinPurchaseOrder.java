package cn.qihangerp.open.shein.helper.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SheinPurchaseOrder {

    @JsonProperty("addTime")
    private String addTime;
    @JsonProperty("addUid")
    private String addUid;
    @JsonProperty("allocateTime")
    private String allocateTime;
    @JsonProperty("category")
    private Integer category;
    @JsonProperty("categoryName")
    private String categoryName;
    @JsonProperty("checkTime")
    private String checkTime;
    @JsonProperty("currencyId")
    private Integer currencyId;
    @JsonProperty("currencyName")
    private String currencyName;
    @JsonProperty("customInfoId")
    private String customInfoId;
    @JsonProperty("deliveryTime")
    private String deliveryTime;
    @JsonProperty("firstMark")
    private Integer firstMark;
    @JsonProperty("firstMarkName")
    private String firstMarkName;
    @JsonProperty("isJitMotherName")
    private String isJitMotherName;
    @JsonProperty("orderExtends")
    private List<OrderExtendsDTO> orderExtends;
    @JsonProperty("orderMarkId")
    private Integer orderMarkId;
    @JsonProperty("orderMarkName")
    private String orderMarkName;
    @JsonProperty("orderNo")
    private String orderNo;
    @JsonProperty("orderSupervisor")
    private String orderSupervisor;
    @JsonProperty("prepareTypeId")
    private Integer prepareTypeId;
    @JsonProperty("prepareTypeName")
    private String prepareTypeName;
    @JsonProperty("receiptTime")
    private String receiptTime;
    @JsonProperty("recommendedSubWarehouseId")
    private Integer recommendedSubWarehouseId;
    @JsonProperty("requestDeliveryTime")
    private String requestDeliveryTime;
    @JsonProperty("requestTakeParcelTime")
    private String requestTakeParcelTime;
    @JsonProperty("reserveTime")
    private String reserveTime;
    @JsonProperty("returnTime")
    private String returnTime;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("statusName")
    private String statusName;
    @JsonProperty("storageId")
    private Integer storageId;
    @JsonProperty("storageTime")
    private String storageTime;
    @JsonProperty("supplierName")
    private String supplierName;
    @JsonProperty("type")
    private Integer type;
    @JsonProperty("typeName")
    private String typeName;
    @JsonProperty("updateTime")
    private String updateTime;
    @JsonProperty("urgentTypeName")
    private String urgentTypeName;
    @JsonProperty("warehouseName")
    private String warehouseName;

    @NoArgsConstructor
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderExtendsDTO {
        @JsonProperty("defectiveQuantity")
        private Integer defectiveQuantity;
        @JsonProperty("deliveryQuantity")
        private Integer deliveryQuantity;
        @JsonProperty("imgPath")
        private String imgPath;
        @JsonProperty("orderQuantity")
        private Integer orderQuantity;
        @JsonProperty("price")
        private BigDecimal price;
        @JsonProperty("receiptQuantity")
        private Integer receiptQuantity;
        @JsonProperty("remark")
        private String remark;
        @JsonProperty("skc")
        private String skc;
        @JsonProperty("skuCode")
        private String skuCode;
        @JsonProperty("storageQuantity")
        private Integer storageQuantity;
        @JsonProperty("suffixZh")
        private String suffixZh;
        @JsonProperty("supplierCode")
        private String supplierCode;
        @JsonProperty("supplierSku")
        private String supplierSku;
    }
}
