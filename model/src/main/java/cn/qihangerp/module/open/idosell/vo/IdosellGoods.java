package cn.qihangerp.module.open.idosell.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * IdoSell商品信息表
 * </p>
 *
 * @author CodeGenerator
 * @since 2025-02-08
 */
@Getter
@Setter
@TableName("oms_idosell_goods")
@ApiModel(value = "IdosellGoods对象", description = "IdoSell商品信息表")
public class IdosellGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("IdoSell商品ID")
    @TableField("product_id")
    private String productId;

    @ApiModelProperty("商品名称")
    @TableField("product_name")
    private String productName;

    @ApiModelProperty("商品描述")
    @TableField("product_desc")
    private String productDesc;

    @ApiModelProperty("IdoSell分类ID")
    @TableField("category_id")
    private Long categoryId;

    @ApiModelProperty("分类名称")
    @TableField("category_name")
    private String categoryName;

    @ApiModelProperty("商品编号")
    @TableField("goods_num")
    private String goodsNum;


    @ApiModelProperty("商品SKC编号")
    @TableField("product_displayed_code")
    private String productDisplayedCode;

    @ApiModelProperty("零售价")
    @TableField("retail_price")
    private BigDecimal retailPrice;

    @ApiModelProperty("批发价")
    @TableField("wholesale_price")
    private BigDecimal wholesalePrice;

    @ApiModelProperty("主图URL")
    @TableField("main_image")
    private String mainImage;

    @ApiModelProperty("详情图URLs，JSON格式")
    @TableField("detail_images")
    private String detailImages;

    @ApiModelProperty("同步状态：0-未同步到ERP 1-已同步 2-同步失败")
    @TableField("sync_status")
    private Byte syncStatus;

    @ApiModelProperty("ERP商品ID")
    @TableField("o_goods_id")
    private Long oGoodsId;

    @ApiModelProperty("同步错误信息")
    @TableField("error_msg")
    private String errorMsg;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @ApiModelProperty("店铺id（sys_shop表id）")
    @TableField("shop_id")
    private Long shopId;

    @TableField("product_is_deleted")
    private String productIsDeleted;
    @TableField("deliverer_id")
    private Integer delivererId;
    @TableField("deliverer_name")
    private String delivererName;
    @TableField("product_vat")
    private Integer productVat;
    @TableField("product_vat_free")
    private String productVatFree;
    @TableField("product_is_visible")
    private String productIsVisible;
    @TableField("product_adding_time")
    private String productAddingTime;
    @TableField("product_quantity_changed_time")
    private String productQuantityChangedTime;
    @TableField("currency_id")
    private String currencyId;


    @TableField(exist = false)
    private List<IdosellGoodsSku> skuList;
}
