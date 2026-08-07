package cn.qihangerp.module.open.shein.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName oms_shein_shop_category
 */
@TableName(value ="oms_shein_shop_category")
@Data
public class OmsSheinShopCategory implements Serializable {
    /**
     * 
     */
    @TableId
    private Long categoryId;

    /**
     * 
     */
    private String categoryName;

    /**
     * 
     */
    private String children;

    /**
     * 
     */
    private String lastCategory;

    /**
     * 
     */
    private Long parentCategoryId;

    /**
     * 
     */
    private Long productTypeId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 店铺id
     */
    private Long shopId;
    private Long omsCategoryId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}