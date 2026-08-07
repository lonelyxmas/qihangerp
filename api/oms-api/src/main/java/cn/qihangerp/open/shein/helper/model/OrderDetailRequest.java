package cn.qihangerp.open.shein.helper.model;

import lombok.Data;

import java.util.List;

@Data
public class OrderDetailRequest extends BaseRequest{

    /**
     * 订单号列表；最多30条；
     */
    private List<String> orderNoList;
} 