package cn.qihangerp.module.goods.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * OMS商品SKU表
 * @TableName o_goods_sku
 */
@TableName(value ="o_goods_sku")
@Data
public class OGoodsSku implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 外键（o_goods）
     */
    private Long goodsId;

    /**
     * 
     */
    private String goodsNum;

    /**
     * 外部erp系统商品id
     */
    private String outerErpGoodsId;

    /**
     * 外部erp系统skuId(唯一)
     */
    private String outerErpSkuId;

    /**
     * 商品名
     */
    private String goodsName;

    /**
     * 规格名
     */
    private String skuName;

    /**
     * 规格编码
     */
    private String skuCode;

    /**
     * 颜色属性id
     */
    private Long colorAttributeId;

    /**
     * 颜色属性
     */
    private String colorAttribute;

    /**
     * 颜色值id
     */
    private Long colorValueId;

    /**
     * 颜色值
     */
    private String colorValue;

    /**
     * 颜色图片
     */
    private String colorImage;

    /**
     * 尺码属性id
     */
    private Long sizeAttributeId;

    /**
     * 尺码属性
     */
    private String sizeAttribute;

    /**
     * 尺码值id
     */
    private Long sizeValueId;

    /**
     * 尺码值
     */
    private String sizeValue;

    /**
     * 款式属性id
     */
    private Long styleAttributeId;

    /**
     * 款式属性
     */
    private String styleAttribute;

    /**
     * 款式值id
     */
    private Long styleValueId;

    /**
     * 款式值
     */
    private String styleValue;

    /**
     * 库存条形码
     */
    private String barCode;

    /**
     * 预计采购价格
     */
    private BigDecimal purPrice;

    /**
     * 建议零售价
     */
    private BigDecimal retailPrice;
    private BigDecimal wholePrice;

    /**
     * 单位成本
     */
    private BigDecimal unitCost;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 最低库存（预警）
     */
    private Integer lowQty;

    /**
     * 最高库存（预警）
     */
    private Integer highQty;

    /**
     * erp商品体积
     */
    private String volume;

    /**
     * 衣长/裙长/裤长
     */
    private Double length;

    /**
     * 高度/袖长
     */
    private Double height;

    /**
     * 宽度/胸阔(围)
     */
    private Double width;

    /**
     * 重量
     */
    private Double weight;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}