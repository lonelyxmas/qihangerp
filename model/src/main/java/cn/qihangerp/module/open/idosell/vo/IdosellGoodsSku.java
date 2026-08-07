package cn.qihangerp.module.open.idosell.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * IdoSell商品SKU表
 * </p>
 *
 * @author CodeGenerator
 * @since 2025-02-09
 */
@Getter
@Setter
@TableName("oms_idosell_goods_sku")
@ApiModel(value = "IdosellGoodsSku对象", description = "IdoSell商品SKU表")
public class IdosellGoodsSku implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("IdoSell商品ID")
    @TableField("product_id")
    private String productId;

    @ApiModelProperty("商品编码")
    @TableField("goods_num")
    private String goodsNum;

    @ApiModelProperty("SKU ID")
    @TableField("sku_id")
    private String skuId;

    @ApiModelProperty("颜色ID")
    @TableField("color_id")
    private String colorId;

    @ApiModelProperty("颜色名称")
    @TableField("color_name")
    private String colorName;

    @ApiModelProperty("颜色")
    @TableField("color")
    private String color;

    @ApiModelProperty("尺码ID")
    @TableField("size_id")
    private String sizeId;
    @ApiModelProperty("尺码主键ID")
    @TableField("size_value_id")
    private String sizeValueId;

    @ApiModelProperty("尺码名称")
    @TableField("size_name")
    private String sizeName;
    @ApiModelProperty("尺码名称")
    @TableField("size_panel_name")
    private String sizePanelName;

    @ApiModelProperty("库存数量")
    @TableField("stock_quantity")
    private Integer stockQuantity;

    @ApiModelProperty("零售价")
    @TableField("retail_price")
    private BigDecimal retailPrice;

    @ApiModelProperty("批发价")
    @TableField("wholesale_price")
    private BigDecimal wholesalePrice;

    @ApiModelProperty("ERP SKU ID")
    @TableField("o_goods_id")
    private Long oGoodsId;

    @ApiModelProperty("ERP SKU ID")
    @TableField("o_goods_sku_id")
    private Long oGoodsSkuId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @ApiModelProperty("店铺id")
    @TableField("shop_id")
    private Integer shopId;



    @TableField("stock_id")
    private Integer stockId;

    @TableField("product_size_code_external")
    private String productSizeCodeExternal;

    @ApiModelProperty("同步状态：0-未同步到ERP 1-已同步 2-同步失败")
    @TableField("sync_status")
    private Byte syncStatus;
}
