package cn.qihangerp.open.shein.helper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductListRequest extends BaseRequest{
    @JsonProperty("pageNum")
    private Integer pageNum;
    
    @JsonProperty("pageSize")
    private Integer pageSize;
    
    @JsonProperty("insertTimeStart")
    private String insertTimeStart;
    
    @JsonProperty("insertTimeEnd")
    private String insertTimeEnd;
    
    @JsonProperty("updateTimeStart")
    private String updateTimeStart;
    
    @JsonProperty("updateTimeEnd")
    private String updateTimeEnd;
} 