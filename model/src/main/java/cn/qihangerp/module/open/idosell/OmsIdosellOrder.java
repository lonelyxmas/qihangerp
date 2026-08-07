package cn.qihangerp.module.open.idosell.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 
 * @TableName oms_idosell_order
 */
@TableName(value ="oms_idosell_order")
@Data
public class OmsIdosellOrder implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String orderId;

    /**
     * 
     */
    private Long orderSerialNumber;

    /**
     * 
     */
    private String orderType;

    /**
     * 
     */
    private Long clientId;

    /**
     * 
     */
    private String clientLogin;

    /**
     * 
     */
    private String clientEmail;

    /**
     * 
     */
    private String clientPhone1;

    /**
     * 
     */
    private String clientPhone2;

    /**
     * 
     */
    private String clientCodeExternal;

    /**
     * 
     */
    private String billingFirstName;

    /**
     * 
     */
    private String billingLastName;

    /**
     * 
     */
    private String billingFirm;

    /**
     * 
     */
    private String billingStreet;

    /**
     * 
     */
    private String billingCity;

    /**
     * 
     */
    private String billingZipCode;

    /**
     * 
     */
    private String billingCountryId;

    /**
     * 
     */
    private String billingCountryName;

    /**
     * 
     */
    private String billingProvince;

    /**
     * 
     */
    private String billingProvinceId;

    /**
     * 
     */
    private String billingPhone1;

    /**
     * 
     */
    private String billingPhone2;

    /**
     * 
     */
    private String billingNip;

    /**
     * 
     */
    private String deliveryAddressId;

    /**
     * 
     */
    private String deliveryFirstName;

    /**
     * 
     */
    private String deliveryLastName;

    /**
     * 
     */
    private String deliveryFirm;

    /**
     * 
     */
    private String deliveryStreet;

    /**
     * 
     */
    private String deliveryCity;

    /**
     * 
     */
    private String deliveryZipCode;

    /**
     * 
     */
    private String deliveryCountryId;

    /**
     * 
     */
    private String deliveryCountryName;

    /**
     * 
     */
    private String deliveryProvince;

    /**
     * 
     */
    private String deliveryProvinceId;

    /**
     * 
     */
    private String deliveryPhone1;

    /**
     * 
     */
    private String deliveryPhone2;

    /**
     * 
     */
    private String deliveryAddressType;

    /**
     * 
     */
    private Long deliveryPickupPointInternalId;

    /**
     * 
     */
    private String orderStatus;

    /**
     * 
     */
    private LocalDateTime orderAddDate;

    /**
     * 
     */
    private LocalDateTime orderChangeDate;

    /**
     * 
     */
    private Long orderDispatchDate;

    /**
     * 
     */
    private Integer orderPrepareTime;

    /**
     * 
     */
    private String clientNoteToCourier;

    /**
     * 
     */
    private String clientNoteToOrder;

    /**
     * 
     */
    private String clientRequestInvoice;

    /**
     * 
     */
    private String orderNote;

    /**
     * 
     */
    private String orderOperatorLogin;

    /**
     * 
     */
    private String apiFlag;

    /**
     * 
     */
    private String productRemovedInStock;

    /**
     * 
     */
    private String orderBridgeNote;

    /**
     * 
     */
    private String billingCurrency;

    /**
     * 
     */
    private BigDecimal currencyRate;

    /**
     * 
     */
    private BigDecimal orderDeliveryCost;

    /**
     * 
     */
    private Integer orderDeliveryVat;

    /**
     * 
     */
    private Integer orderInsuranceCost;

    /**
     * 
     */
    private Integer orderInsuranceVat;

    /**
     * 
     */
    private Integer orderPayformCost;

    /**
     * 
     */
    private Integer orderPayformVat;

    /**
     * 
     */
    private BigDecimal orderProductsCost;

    /**
     * 
     */
    private Integer orderPaymentDays;

    /**
     * 
     */
    private String orderPaymentType;

    /**
     * 
     */
    private Integer orderRebatePercent;

    /**
     * 
     */
    private String orderVatExists;

    /**
     * 
     */
    private String orderWorthCalculateType;

    /**
     * 
     */
    private Long courierId;

    /**
     * 
     */
    private String courierName;

    /**
     * 
     */
    private boolean courierWebserviceOnly;

    /**
     * 
     */
    private String deliveryDate;

    /**
     * 
     */
    private String deliveryDateAdditional;

    /**
     * 
     */
    private String deliveryPackageId;

    /**
     * 
     */
    private Integer deliveryWeight;

    /**
     * 
     */
    private String estimatedDeliveryDate;

    /**
     * 
     */
    private Long sourceOrderId;

    /**
     * 
     */
    private String sourceName;

    /**
     * 
     */
    private String sourceType;

    /**
     * 
     */
    private Integer sourceTypeId;

    /**
     * 
     */
    private String sourcePageUrl;

    /**
     * 
     */
    private Long shopId;

    /**
     * 
     */
    private Long entryProductId;

    /**
     * 
     */
    private String sourceFresh;

    /**
     * 
     */
    private String sourceFulfillment;

    /**
     * 
     */
    private LocalDateTime createTime;

    /**
     * 
     */
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<OmsIdosellOrderItem> items;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}