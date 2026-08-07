package cn.qihangerp.module.open.idosell.domain.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class IdosellGoodsStockBo implements Serializable {
    /**
     * 商品数字id
     */
    private Long productId;
    private Long shopId;
    private String skuCode;
    private String goodsNum;

}
