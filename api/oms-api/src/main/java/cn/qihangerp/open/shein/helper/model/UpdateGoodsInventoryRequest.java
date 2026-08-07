package cn.qihangerp.open.shein.helper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateGoodsInventoryRequest extends BaseRequest{

    @JsonProperty("stock")
    private List<UpdateGoodsInventoryHolder> stock;

    @Data
    public static class UpdateGoodsInventoryHolder {

        @JsonProperty("skc")
        private String skc;

        @JsonProperty("shein_sku")
        private String skuCode;

        @JsonProperty("available_number")
        private String availableNumber;
        @JsonProperty("stock_type")
        private String stock_type="3";
    }
} 