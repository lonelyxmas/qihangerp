package cn.qihangerp.open.idosell.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class SizeAttributeResponse {

    @JsonProperty("last_changed_time")
    private String lastChangedTime;
    @JsonProperty("size_groups")
    private List<SizeGroupsDTO> sizeGroups;

    @NoArgsConstructor
    @Data
    public static class SizeGroupsDTO {
        @JsonProperty("group_id")
        private Integer groupId;
        @JsonProperty("group_name")
        private String groupName;
        @JsonProperty("sizes")
        private List<SizesDTO> sizes;

        @NoArgsConstructor
        @Data
        public static class SizesDTO {
            @JsonProperty("lang_data")
            private List<LangDataDTO> langData;
            @JsonProperty("size_id")
            private String sizeId;
            @JsonProperty("size_name")
            private String sizeName;

            @NoArgsConstructor
            @Data
            public static class LangDataDTO {
                @JsonProperty("lang_id")
                private String langId;
                @JsonProperty("name")
                private String name;
            }
        }
    }
}
