package cn.qihangerp.open.shein.request;

import lombok.Data;

@Data
public class CategoryRequest {
    private Long categoryId;
    private Long productTypeId;
    private Long shopId;
}
