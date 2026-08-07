package cn.qihangerp.module.goods.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 店铺分类属性值（color和size）关联关系
 * @TableName o_shop_category_attribute_value_relation
 */
@TableName(value ="o_goods_attribute_value_relation")
@Data
public class OGoodsAttributeValueRelation implements Serializable {
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
    private Long shopAttributeValueId;

    /**
     * 商品库分类属性ID
     */
    private Long attributeValueId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}