package cn.qihangerp.module.open.shein.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * SHEIN采购订单商品表
 * @TableName oms_shein_purchase_order_item
 */
@TableName(value ="oms_shein_purchase_order_item")
@Data
public class OmsSheinPurchaseOrderItem implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 商品SKC
     */
    private String skc;

    /**
     * SKU编码
     */
    private String skuCode;

    /**
     * 供应商编码
     */
    private String supplierCode;

    /**
     * 供应商SKU
     */
    private String supplierSku;

    /**
     * 中文后缀(颜色-尺码)
     */
    private String suffixZh;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 需求数量
     */
    private Integer needQuantity;

    /**
     * 订单数量
     */
    private Integer orderQuantity;

    /**
     * 发货数量
     */
    private Integer deliveryQuantity;

    /**
     * 收货数量
     */
    private Integer receiptQuantity;

    /**
     * 入库数量
     */
    private Integer storageQuantity;

    /**
     * 次品数量
     */
    private Integer defectiveQuantity;

    /**
     * 要求发货数量
     */
    private Integer requestDeliveryQuantity;

    /**
     * 未要求发货数量
     */
    private Integer noRequestDeliveryQuantity;

    /**
     * 已发货数量
     */
    private Integer alreadyDeliveryQuantity;

    /**
     * 图片路径
     */
    private String imgPath;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}