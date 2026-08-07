package cn.qihangerp.open.shein.helper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CategoryResponse {
    private String code;
    private String msg;
    private Info info;
    private Object bbl;
    private String traceId;
    
    @Data
    public static class Info {
        private List<CategoryData> data;
        private Meta meta;
    }
    
    @Data
    public static class CategoryData {
        @JsonProperty("category_id")
        private Long categoryId;
        
        @JsonProperty("category_name")
        private String categoryName;
        
        private List<CategoryData> children;
        
        @JsonProperty("last_category")
        private Boolean lastCategory;
        
        @JsonProperty("parent_category_id")
        private Long parentCategoryId;
        
        @JsonProperty("product_type_id")
        private Long productTypeId;
    }
    
    @Data
    public static class Meta {
        private Integer count;
        private Object customObj;
    }
} 