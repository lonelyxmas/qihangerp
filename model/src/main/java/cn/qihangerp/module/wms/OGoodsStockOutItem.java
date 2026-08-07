package cn.qihangerp.module.wms.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 出库单明细
 * @TableName o_goods_stock_out_item
 */
@TableName(value ="o_goods_stock_out_item")
@Data
public class OGoodsStockOutItem implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 出库类型1订单出库2采购退货出库3盘点出库4报损出库
     */
    private Integer stockOutType;

    /**
     * 出库单id（外键）
     */
    private Long stockOutId;

    /**
     * 来源订单id
     */
    private Long sourceOrderId;

    /**
     * 来源订单itemId出库对应的itemId，如：order_item表id、invoice_info表id
     */
    private Long sourceOrderItemId;

    /**
     * 来源订单号
     */
    private String sourceOrderNum;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 
     */
    private String goodsName;

    /**
     * 
     */
    private String goodsImg;

    /**
     * 商品编码
     */
    private String goodsNum;

    /**
     * 商品规格id
     */
    private Long skuId;

    /**
     * 规格编码
     */
    private String skuNum;

    /**
     * 总数量
     */
    private Integer quantity;

    /**
     * 已出库数量
     */
    private Integer outQuantity;

    /**
     * 完成出库时间
     */
    private Date completeTime;

    /**
     * 完成拣货时间
     */
    private Date pickedTime;

    /**
     * 状态：0待出库1部分出库2全部出库
     */
    private Integer status;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}