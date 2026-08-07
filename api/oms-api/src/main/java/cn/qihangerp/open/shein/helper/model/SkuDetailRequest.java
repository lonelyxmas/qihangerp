package cn.qihangerp.open.shein.helper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SkuDetailRequest extends BaseRequest{
    @JsonProperty("skuCodes")
    private List<String> skuCodes;

    @JsonProperty("language")
    private String language;
} 