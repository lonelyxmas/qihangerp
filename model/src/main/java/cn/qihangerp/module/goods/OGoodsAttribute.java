package cn.qihangerp.module.goods.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 商品属性表
 * @TableName o_goods_attribute
 */
@TableName(value ="o_goods_attribute")
@Data
public class OGoodsAttribute implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long attributeId;

    /**
     * 属性录入方式;0: 手工填写参数；1:下拉列表选择(可多选);2:销售属性专属(只针对销售属性，下拉列表选择);3:下拉列表选择(单选)4:下拉列表+手工参数
     */
    private Integer attributeMode;

    /**
     * 属性的类型 1-销售属性（在skc和sku维度传参），2-尺寸属性（在size维度传参），3-成分属性（在商品属性维度传参），4-普通属性（在商品属性维度传参）
     */
    private Integer attributeType;

    /**
     * 属性的名称，常见的销售属性有颜色、尺码；常见的商品属性有产品特征、产品功能、是否需要电源等
     */
    private String attributeName;

    /**
     * 判断属性是否必填 1:属性不填; 2:属性选填; 3:属性必填；必填属性如果不填，则会报错；
     */
    private Integer attributeStatus;

    /**
     * 用这个字段判断是否是主销售属性（skc维度属性）。1:主销售属性(skc维度的销售属性，比如颜色)，主销售属性也可用于次销售属性; 0: 不是主销售属性； 如果“attribute_type=1, attribute_label=0”,则说明这个属性为次销售属性（用于sku维度的销售属性传参，比如尺码）
     */
    private Integer attributeLabel;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}