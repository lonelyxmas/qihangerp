package cn.qihangerp.open.shein.helper.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PurchaseOrderListRequest {

    /**
     * 采购单类型；1：急采/ 2：备货
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("type")
    private Integer type;

    /**
     * 开始时间;示例：2024-12-12 15:38:29（UTC+8）
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("updateTimeStart")
    private String updateTimeStart;

    /**
     * 结束时间;示例：2024-12-12 15:38:29（UTC+8）
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("updateTimeEnd")
    private String updateTimeEnd;

    /**
     * 分页的页数
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("pageNumber")
    private Integer pageNumber;

    /**
     * 每页返回的条数；请设置1～30的整数
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("pageSize")
    private Integer pageSize;
} 