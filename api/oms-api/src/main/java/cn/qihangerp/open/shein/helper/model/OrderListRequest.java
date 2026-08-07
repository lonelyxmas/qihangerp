package cn.qihangerp.open.shein.helper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrderListRequest extends BaseRequest{

    /**
     * 查询类型：1：根据订单创建时间查询/ 2：根据订单更新时间查询
     */
    @JsonProperty("queryType")
    private Integer queryType;

    /**
     * 开始时间;示例：2024-12-12 15:38:29（UTC+8）
     */
    @JsonProperty("startTime")
    private String startTime;

    /**
     * 结束时间;示例：2024-12-12 15:38:29（UTC+8）
     */
    @JsonProperty("endTime")
    private String endTime;

    /**
     * 分页的页数
     */
    @JsonProperty("page")
    private Integer page;

    /**
     * 每页返回的条数；请设置1～30的整数
     */
    @JsonProperty("pageSize")
    private Integer pageSize;
} 