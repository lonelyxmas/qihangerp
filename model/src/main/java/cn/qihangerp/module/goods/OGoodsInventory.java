package cn.qihangerp.module.goods.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 商品库存表
 * @TableName o_goods_inventory
 */
@TableName(value ="o_goods_inventory")
@Data
public class OGoodsInventory implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 
     */
    private String goodsName;

    /**
     * 商品编码
     */
    private String goodsNum;

    /**
     * 商品规格id
     */
    private Long skuId;

    /**
     * 规格编码（唯一）
     */
    private String skuCode;

    /**
     * 图片
     */
    private String colorImage;

    /**
     * 颜色名称
     */
    private String colorValue;

    /**
     * 尺码值
     */
    private String sizeValue;

    /**
     * 当前库存
     */
    private Integer quantity;

    /**
     * 0正常  1删除
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateBy;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}