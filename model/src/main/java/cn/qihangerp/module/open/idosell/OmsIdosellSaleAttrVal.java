package cn.qihangerp.module.open.idosell.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * idosell销售属性值
 * @TableName oms_idosell_sale_attr_val
 */
@TableName(value ="oms_idosell_sale_attr_val")
@Data
public class OmsIdosellSaleAttrVal implements Serializable {
    /**
     * 属性值id
     */
    @TableId(type = IdType.AUTO)
    private Long attributeValueId;

    /**
     * 属性id
     */
    private Long attributeId;

    /**
     * 属性值信息
     */
    private String attributeValue;

    /**
     * 尺寸名称
     */
    private String sizePanelName;

    /**
     * 是否展示
     */
    private Integer isShow;

    private Integer groupId;
    private String groupName;
    private String langData;

    /**
     * 判断是否是自定义属性值，true代表是，false代表否
     */
    private String isCustomAttributeValue;
    private Long omsAttributeValueId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}