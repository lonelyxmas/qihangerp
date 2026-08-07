package cn.qihangerp.module.open.idosell.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * IdoSell商品SKU库存表
 * @TableName oms_idosell_goods_sku_stock
 */
@TableName(value ="oms_idosell_goods_sku_stock")
@Data
public class OmsIdosellGoodsSkuStock implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * IdoSell商品ID
     */
    private String productId;

    /**
     * 
     */
    private String productName;

    /**
     * 分解后的商品编码
     */
    private String goodsNum;

    /**
     * SKU code
     */
    private String skuCode;

    /**
     * 图片
     */
    private String colorImage;

    /**
     * 颜色名称
     */
    private String colorValue;

    /**
     * 尺码ID
     */
    private String sizeId;

    /**
     * 尺码名称
     */
    private String sizeName;

    /**
     * 库存数量
     */
    private Integer quantity;

    /**
     * idosell stock id修改库存参数
     */
    private Integer stockId;
    private Long shopId;

    /**
     * idosell 修改库存参数
     */
    private String productSizeCode;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}