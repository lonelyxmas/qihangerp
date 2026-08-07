package cn.qihangerp.module.open.idosell.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName oms_idosell_shop_category
 */
@TableName(value ="oms_idosell_shop_category")
@Data
public class OmsIdosellShopCategory implements Serializable {
    /**
     * 
     */
    @TableId
    private Integer id;

    /**
     * 
     */
    private Integer parentId;

    /**
     * 
     */
    private Integer priority;

    /**
     * 
     */
    private String pkwiu;

    /**
     * 
     */
    private Integer productCount;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 语言数据
     */
    private String langData;

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