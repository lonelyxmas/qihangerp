package cn.qihangerp.module.open.idosell.domain.bo;

import lombok.Data;

@Data
public class OrderSearchParam {
    private String orderId;
    private Long shopId;
    private String orderStatus;
    private String startTime;
    private String endTime;
}
