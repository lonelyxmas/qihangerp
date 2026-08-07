package cn.qihangerp.module.open.shein.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName oms_shein_product_type_attr_val
 */
@TableName(value ="oms_shein_product_attr_val")
@Data
public class OmsSheinProductAttrVal implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long attributeValueId;

    /**
     * 
     */
    private Long attributeId;

    /**
     * 
     */
    private String attributeValue;

    /**
     * 
     */
    private Integer isShow;

    /**
     * 
     */
    private String isCustomAttributeValue;

    /**
     * 
     */
    private Long supplierId;
    private Long omsAttributeValueId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}