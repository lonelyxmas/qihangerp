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
 * shein商品sku表
 * @TableName oms_shein_goods_sku
 */
@TableName(value ="oms_shein_goods_sku")
@Data
public class OmsSheinGoodsSku implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * spu名称
     */
    private String spuName;

    /**
     * skc名称
     */
    private String skcName;

    /**
     * sku名称
     */
    private String skuCode;

    /**
     * 卖家SKC编码，创建商品时需要商家维护，没有维护则不返回
     */
    private String supplierCode;

    /**
     * 卖家SKU，创建商品时需要商家维护，没有维护则不返回
     */
    private String supplierSku;

    /**
     * 商品图片信息JSON
     */
    private String skcImage;

    /**
     * skc销售属性ID
     */
    private String colorId;

    /**
     * skc销售属性值ID
     */
    private String colorValueId;

    /**
     * sku图片
     */
    private String colorImage;

    /**
     * skc销售属性值
     */
    private String colorValue;

    /**
     * 主销售属性多语言名称
     */
    private String colorAttribute;

    /**
     * 主销售属性值多语言名称
     */
    private String colorAttributeValue;

    /**
     * sku尺码销售属性ID
     */
    private String sizeId;

    /**
     * sku尺码销售属性值ID
     */
    private String sizeValueId;
    private String sizeValue;

    /**
     * 次销售属性列表
     */
    private String saleAttributeList;

    /**
     * 上下架信息
     */
    private String shelfStatusInfoList;

    /**
     * 商城销售状态，1-在售 2-停售
     */
    private Integer mallState;

    /**
     * 	
采购状态，1-在采 2-停采
     */
    private Integer stopPurchase;

    /**
     * 供货价信息列表
     */
    private String costInfoList;

    /**
     * 供货价
     */
    private BigDecimal costPriceEur;

    /**
     * 供货价
     */
    private BigDecimal costPriceCny;

    /**
     * 库存数；只返回部分备货到shein仓或不备货到shein仓的商品库存
     */
    private String inventoryQuantity;

    /**
     * 已销售未出库锁定库存数
     */
    private String lockedQuantity;

    /**
     * 可用库存
     */
    private String usableInventory;

    /**
     * 已下单未支付锁定库存数
     */
    private String tempLockQuantity;

    /**
     * 长
     */
    private String length;

    /**
     * 宽
     */
    private String width;

    /**
     * 高
     */
    private String height;

    /**
     * 重量
     */
    private String weight;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 商品skuid(o_goods_sku外键)
     */
    private Long oGoodsSkuId;
    private Long oGoodsId;
    private Byte syncStatus;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}