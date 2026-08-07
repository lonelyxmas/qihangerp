package cn.qihangerp.module.open.shein.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 库存信息表
 * @TableName oms_shein_goods_sku_stock
 */
@TableName(value ="oms_shein_goods_sku_stock")
@Data
public class OmsSheinGoodsSkuStock implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品SKC编码
     */
    private String skcName;

    /**
     * 商品SPU名称
     */
    private String spuName;

    /**
     * SKU编码
     */
    private String skuCode;

    /**
     * 总库存数量
     */
    private Integer totalInventoryQuantity;

    /**
     * 总锁定数量
     */
    private Integer totalLockedQuantity;

    /**
     * 总缺货数量
     */
    private Integer totalOutOfStockQty;

    /**
     * 总临时锁定数量
     */
    private Integer totalTempLockQuantity;

    /**
     * 总可用库存
     */
    private Integer totalUsableInventory;

    /**
     * 仓库编码
     */
    private String warehouseCode;

    /**
     * 仓库类型
     */
    private String warehouseType;

    /**
     * 库存数量
     */
    private Integer inventoryQuantity;

    /**
     * 锁定数量
     */
    private Integer lockedQuantity;

    /**
     * 缺货数量
     */
    private Integer outOfStockQty;

    /**
     * 临时锁定数量
     */
    private Integer tempLockQuantity;

    /**
     * 可用库存
     */
    private Integer usableInventory;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 卖家SKC编码，创建商品时需要商家维护，没有维护则不返回
     */
    private String supplierCode;

    /**
     * 卖家SKU，创建商品时需要商家维护，没有维护则不返回
     */
    private String supplierSku;

    /**
     * sku图片
     */
    private String colorImage;

    /**
     * skc销售属性值
     */
    private String colorValue;

    /**
     * 尺码值
     */
    private String sizeValue;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}