package cn.qihangerp.module.open.idosell.domain;

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
 * 
 * @TableName oms_idosell_order_item
 */
@TableName(value ="oms_idosell_order_item")
@Data
public class OmsIdosellOrderItem implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单主表id
     */
    private Long orderId;

    /**
     * 
     */
    private Long productId;

    /**
     * 
     */
    private String productCode;

    /**
     * 
     */
    private String productName;

    /**
     * 
     */
    private String versionName;

    /**
     * 
     */
    private String sizeId;

    /**
     * 
     */
    private String sizePanelName;

    /**
     * 
     */
    private Long stockId;

    /**
     * 
     */
    private Integer productQuantity;

    /**
     * 
     */
    private BigDecimal productOrderPrice;

    /**
     * 
     */
    private BigDecimal productOrderPriceNet;

    /**
     * 
     */
    private BigDecimal productPanelPrice;

    /**
     * 
     */
    private BigDecimal productPanelPriceNet;

    /**
     * 
     */
    private Integer productVat;

    /**
     * 
     */
    private Integer productWeight;

    /**
     * 
     */
    private Integer basketPosition;

    /**
     * 
     */
    private Long bundleId;

    /**
     * 
     */
    private String orderSalesMode;

    /**
     * 
     */
    private String productSizeCodeExternal;

    /**
     * 
     */
    private String remarksToProduct;

    /**
     * 
     */
    private LocalDateTime createTime;

    /**
     * 
     */
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}