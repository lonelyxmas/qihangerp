package cn.qihangerp.module.goods.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 商品属性值表
 * @TableName o_goods_attribute_value
 */
@TableName(value ="o_goods_attribute_value")
@Data
public class OGoodsAttributeValue implements Serializable {
    /**
     * 主键，属性值id
     */
    @TableId(type = IdType.AUTO)
    private Long attributeValueId;

    /**
     * 属性id
     */
    private Long attributeId;

    /**
     * 属性值文本
     */
    private String attributeValue;

    /**
     * 生成SKU的编码
     */
    private String skuCode;
    /**
     * 生成SKC的编码
     */
    private String skcCode;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否删除1删除0未删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}