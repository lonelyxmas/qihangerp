package cn.qihangerp.module.goods.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 店铺分类属性值（color和size）关联关系
 * @TableName o_goods_attribute_relation
 */
@TableName(value ="o_goods_attribute_relation")
@Data
public class OGoodsAttributeRelation implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 平台
     */
    private Integer shopPlatformId;

    /**
     * 平台店铺分类属性ID
     */
    private Long shopAttributeId;

    /**
     * 商品库分类属性ID
     */
    private Long attributeId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}