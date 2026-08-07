package cn.qihangerp.module.open.idosell.domain.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class IdosellGoodsBo implements Serializable {
    private Integer shopId;
    private Integer productId;
    private Integer categoryId;
    private String goodsNum; // 商品编号
}
