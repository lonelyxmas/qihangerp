package cn.qihangerp.module.open.shein.domain.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SheinGoodsStockBo implements Serializable {
    private String spuName;
    private String goodsNum;
    private String skcName;
    private String skuId;
    private String skuCode;
}
