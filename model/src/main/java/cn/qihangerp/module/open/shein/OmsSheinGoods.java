package cn.qihangerp.module.open.shein.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * shein商品表
 * @TableName oms_shein_goods
 */
@TableName(value ="oms_shein_goods")
@Data
public class OmsSheinGoods implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品类型ID
     */
    private Long productTypeId;

    /**
     * 末级分类id
     */
    private Long categoryId;

    /**
     * 品牌code
     */
    private String brandCode;

    /**
     * spuName，spuName是SHEIN生成的系统编码
     */
    private String spuName;

    /**
     * 卖家SPU编码，创建商品时需要商家维护，没有维护则不返回
     */
    private String supplierCode;

    /**
     * 商品前台尺寸属性信息
     */
    private String dimensionAttributeInfoList;

    /**
     * 商品属性信息
     */
    private String productAttributeInfoList;

    /**
     * 商品多语言描述
     */
    private String productMultiDescList;

    /**
     * 商品多语言名称
     */
    private String productMultiNameList;

    /**
     * spu图片组信息
     */
    private String spuImageInfolist;

    /**
     * 商品主图
     */
    private String productImage;
    private String detailImages;

    /**
     * 商品名称（英文）
     */
    private String productName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * erp订单商品id
     */
    private Long oGoodsId;

    /**
     * 公文状态：-1：接受失败  1：(待审核) 2：(审批成功) 3：(审批失败） 4：(已撤回) 5：申诉中
     */
    private Integer checkStatus;

    /**
     * 
     */
    private String oSkuCode;

    private Long shopId;
    private Byte syncStatus;

    /**
     * 供货价
     */
    private BigDecimal costPriceEur;

    /**
     * 供货价
     */
    private BigDecimal costPriceCny;


    @TableField(exist = false)
    private List<OmsSheinGoodsSku> skuList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}