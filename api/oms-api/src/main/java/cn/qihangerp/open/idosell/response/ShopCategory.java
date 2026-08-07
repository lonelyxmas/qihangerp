package cn.qihangerp.open.idosell.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Data
public class ShopCategory {


    @JsonProperty("id")
    private Integer id;
    @JsonProperty("parent_id")
    private Integer parentId;
    @JsonProperty("priority")
    private Integer priority;
    @JsonProperty("pkwiu")
    private String pkwiu;
    @JsonProperty("product_count")
    private Integer productCount;
    @JsonProperty("lang_data")
    private List<LangDataDTO> langData;

    @NoArgsConstructor
    @Data
    public static class LangDataDTO {
        @JsonProperty("lang_id")
        private String langId;
        @JsonProperty("singular_name")
        private String singularName;
        @JsonProperty("plural_name")
        private String pluralName;
        @JsonProperty("description")
        private String description;
    }
}
