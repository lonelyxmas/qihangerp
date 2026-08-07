package cn.qihangerp.module.open.shein.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * shein订单item
 * </p>
 *
 * @author CodeGenerator
 * @since 2025-01-31
 */
@Getter
@Setter
@TableName("oms_shein_order_item")
@ApiModel(value = "SheinOrderItem对象", description = "shein订单item")
public class SheinOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("订单号")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty("订单商品id")
    @TableField("goods_id")
    private Long goodsId;

    @ApiModelProperty("sku名称")
    @TableField("sku_code")
    private String skuCode;

    @ApiModelProperty("spu名称")
    @TableField("spu_name")
    private String spuName;

    @ApiModelProperty("订单商品状态")
    @TableField("goods_status")
    private String goodsStatus;

    @ApiModelProperty("销售币种")
    @TableField("seller_currency")
    private String sellerCurrency;

    @ApiModelProperty("卖家币种金额")
    @TableField("seller_currency_price")
    private String sellerCurrencyPrice;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;
}
