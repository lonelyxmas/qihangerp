package cn.qihangerp.open.shein.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class SheinGoodsBo implements Serializable {

    private Integer categoryId;
    private String supplierCode;
    private String spuName;
    private String goodsName;
    private Integer shopId;
}
