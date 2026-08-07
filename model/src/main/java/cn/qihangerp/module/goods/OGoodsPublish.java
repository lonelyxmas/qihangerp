package cn.qihangerp.module.goods.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 商品发布情况
 * @TableName o_goods_publish
 */
@TableName(value ="o_goods_publish")
@Data
public class OGoodsPublish implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     *  发布的平台
     */
    private Integer shopPlatformId;

    /**
     * 平台名
     */
    private String shopPlatform;

    /**
     * 发布的店铺
     */
    private Long shopId;

    /**
     * 店铺名
     */
    private String shopName;

    /**
     * 发布时间
     */
    private Date publishTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}