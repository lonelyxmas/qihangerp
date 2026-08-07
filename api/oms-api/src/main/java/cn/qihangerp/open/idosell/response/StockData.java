package cn.qihangerp.open.idosell.response;

import lombok.Data;

@Data
public class StockData {
    private Integer stockId;
    private String productId;
    private String sizeId;
    private String sizeName;
    private String productSizeCode;
    private Integer quantity;
}
