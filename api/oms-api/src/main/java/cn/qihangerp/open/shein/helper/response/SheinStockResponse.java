package cn.qihangerp.open.shein.helper.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Data
public class SheinStockResponse {

    @JsonProperty("code")
    private Integer code;
    @JsonProperty("info")
    private List<InfoDTO> info;
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("traceId")
    private String traceId;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @NoArgsConstructor
    @Data
    public static class InfoDTO {
        @JsonProperty("goodsInventory")
        private List<GoodsInventoryDTO> goodsInventory;

        @JsonIgnoreProperties(ignoreUnknown = true)
        @NoArgsConstructor
        @Data
        public static class GoodsInventoryDTO {
            @JsonProperty("skcName")
            private String skcName;
            @JsonProperty("skuList")
            private List<SkuListDTO> skuList;
            @JsonProperty("spuName")
            private String spuName;

            @JsonIgnoreProperties(ignoreUnknown = true)
            @NoArgsConstructor
            @Data
            public static class SkuListDTO {
                @JsonProperty("skuCode")
                private String skuCode;
                @JsonProperty("totalInventoryQuantity")
                private Integer totalInventoryQuantity;
                @JsonProperty("totalLockedQuantity")
                private Integer totalLockedQuantity;
                @JsonProperty("totalOutOfStockQty")
                private Integer totalOutOfStockQty;
                @JsonProperty("totalTempLockQuantity")
                private Integer totalTempLockQuantity;
                @JsonProperty("totalUsableInventory")
                private Integer totalUsableInventory;
                @JsonProperty("warehouseInventoryList")
                private List<WarehouseInventoryListDTO> warehouseInventoryList;

                @JsonIgnoreProperties(ignoreUnknown = true)
                @NoArgsConstructor
                @Data
                public static class WarehouseInventoryListDTO {
                    @JsonProperty("inventoryQuantity")
                    private Integer inventoryQuantity;
                    @JsonProperty("lockedQuantity")
                    private Integer lockedQuantity;
                    @JsonProperty("outOfStockQty")
                    private Integer outOfStockQty;
                    @JsonProperty("tempLockQuantity")
                    private Integer tempLockQuantity;
                    @JsonProperty("usableInventory")
                    private Integer usableInventory;
                    @JsonProperty("warehouseCode")
                    private String warehouseCode;
                    @JsonProperty("warehouseType")
                    private String warehouseType;
                }
            }
        }
    }
}
