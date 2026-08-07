package cn.qihangerp.module.wms.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 出库单
 * @TableName o_goods_stock_out
 */
@TableName(value ="o_goods_stock_out")
@Data
public class OGoodsStockOut implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 出库单编号
     */
    private String stockOutNum;

    /**
     * 来源单据号
     */
    private String sourceNum;

    /**
     * 来源单据Id
     */
    private Long sourceId;

    /**
     * 出库类型1订单出库2采购退货出库3盘点出库4报损出库
     */
    private Integer stockOutType;

    /**
     * 商品数
     */
    private Integer goodsGroup;

    /**
     * 商品规格数
     */
    private Integer skuGroup;

    /**
     * 总件数
     */
    private Integer totalQuantity;

    /**
     * 已出库数量
     */
    private Integer outQuantity;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态：0待出库1部分出库2全部出库
     */
    private Integer status;

    /**
     * 打印状态：是否打印1已打印0未打印
     */
    private Integer printStatus;

    /**
     * 打印时间
     */
    private Date printTime;

    /**
     * 出库时间
     */
    private Date outTime;

    /**
     * 完成出库时间
     */
    private Date completeTime;

    /**
     * 出库操作人userid
     */
    private Integer operatorId;

    /**
     * 出库操作人
     */
    private String operatorName;

    /**
     * 创建日期
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