package cn.qihangerp.module.open.shein.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 
 * @TableName oms_shein_product_type_attr
 */
@TableName(value ="oms_shein_product_type_attr")
@Data
public class OmsSheinProductTypeAttr implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long attributeId;

    /**
     * 
     */
    private Long productTypeId;

    /**
     * 
     */
    private String attributeName;

    /**
     * 
     */
    private Integer attributeType;

    /**
     * 
     */
    private Integer attributeLabel;

    /**
     * 
     */
    private Integer attributeStatus;

    /**
     * 
     */
    private Integer attributeMode;

    /**
     * 
     */
    private Integer attributeIsShow;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private List<OmsSheinProductAttrVal> vals;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}