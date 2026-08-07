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
 * SHEIN采购订单主表
 * @TableName oms_shein_purchase_order
 */
@TableName(value ="oms_shein_purchase_order")
@Data
public class OmsSheinPurchaseOrder implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单类型(1:急采)
     */
    private Integer type;

    /**
     * 订单类型名称
     */
    private String typeName;

    /**
     * 币种ID
     */
    private Integer currencyId;

    /**
     * 币种名称
     */
    private String currencyName;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 仓库名称
     */
    private String warehouseName;

    /**
     * 仓库ID
     */
    private Integer storageId;

    /**
     * 要求发货时间
     */
    private Date requestDeliveryTime;

    /**
     * 要求提货时间
     */
    private Date requestTakeParcelTime;

    /**
     * 要求收货时间
     */
    private Date requestReceiptTime;

    /**
     * 要求完成时间
     */
    private Date requestCompleteTime;

    /**
     * 创建时间
     */
    private Date addTime;

    /**
     * 创建人
     */
    private String addUid;

    /**
     * 分配时间
     */
    private Date allocateTime;

    /**
     * 发货时间
     */
    private Date deliveryTime;

    /**
     * 收货时间
     */
    private Date receiptTime;

    /**
     * 验货时间
     */
    private Date checkTime;

    /**
     * 入库时间
     */
    private Date storageTime;

    /**
     * 退货时间
     */
    private Date returnTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 分类(0:急采)
     */
    private Integer category;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 备货类型ID
     */
    private Integer prepareTypeId;

    /**
     * 备货类型名称
     */
    private String prepareTypeName;

    /**
     * 订单标记ID
     */
    private Integer orderMarkId;

    /**
     * 订单标记名称
     */
    private String orderMarkName;

    /**
     * 是否首次(1:是 2:否)
     */
    private Integer firstMark;

    /**
     * 是否首次名称
     */
    private String firstMarkName;

    /**
     * 状态(1:待分配 2:已下单 3:已预定 4:已送货 5:已收货 6:已验货 7:已入库 8:已退货)
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 是否发货(0:否 1:是)
     */
    private Integer isDelivery;

    /**
     * 是否全部发货(0:否 1:是)
     */
    private Integer isAllDelivery;

    /**
     * 是否生产完成(0:否 1:是)
     */
    private Integer isProductionCompletion;

    /**
     * 是否JIT母单(0:否 1:是)
     */
    private Integer isJitMother;

    /**
     * 是否优先生产(0:否 1:是)
     */
    private Integer isPriorProduction;

    /**
     * 店铺id
     */
    private Long shopId;

    @TableField(exist = false)
    private List<OmsSheinPurchaseOrderItem> items;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}