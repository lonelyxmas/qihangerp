package cn.qihangerp.module.goods.domain.bo;

import lombok.Data;

@Data
public class OGoodsInventoryOperateBo {
    private Long id;//库存id
    private Long shopId;//店铺id
    private Integer quantity;//操作的数量
    private Integer type;//操作类型（1增加库存2减少库存3锁定库存）
    private String remark;
}
