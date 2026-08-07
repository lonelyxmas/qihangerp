package cn.qihangerp.module.open.shein.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * shein订单表
 * </p>
 *
 * @author CodeGenerator
 * @since 2025-01-31
 */
@Getter
@Setter
@TableName("oms_shein_order")
@ApiModel(value = "SheinOrder对象", description = "shein订单表")
public class SheinOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("订单号")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty("订单状态")
    @TableField("order_status")
    private String orderStatus;

    @ApiModelProperty("创建时间 2023-04-19 03:37:01")
    @TableField("order_create_time")
    private String orderCreateTime;

    @ApiModelProperty("更新时间 2023-04-19 03:37:01")
    @TableField("order_update_time")
    private String orderUpdateTime;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    @ApiModelProperty("补全状态")
    @TableField("complete_status")
    private Integer completeStatus;
}
