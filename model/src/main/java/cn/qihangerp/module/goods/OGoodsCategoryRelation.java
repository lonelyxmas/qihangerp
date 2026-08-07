package cn.qihangerp.module.goods.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 店铺分类关联关系
 * @TableName o_shop_category_relation
 */
@TableName(value ="o_goods_category_relation")
@Data
public class OGoodsCategoryRelation implements Serializable {
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
     * 平台店铺分类ID
     */
    private Long shopCategoryId;

    /**
     * 商品库分类ID
     */
    private Long categoryId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}