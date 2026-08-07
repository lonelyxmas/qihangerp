package cn.qihangerp.open.shein.helper.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageUploadResponse {
    private String code;
    private String msg;
    private ImageInfo info;
    private Object bbl;
    
    @JsonProperty("traceId")
    private String traceId;
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public ImageInfo getInfo() {
        return info;
    }
    
    public void setInfo(ImageInfo info) {
        this.info = info;
    }
    
    public Object getBbl() {
        return bbl;
    }
    
    public void setBbl(Object bbl) {
        this.bbl = bbl;
    }
    
    public String getTraceId() {
        return traceId;
    }
    
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
    
    public static class ImageInfo {
        @JsonProperty("image_url")
        private String imageUrl;
        
        private int width;
        private int height;
        private long size;
        
        @JsonProperty("image_hex_type")
        private String imageHexType;
        
        public String getImageUrl() {
            return imageUrl;
        }
        
        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
        
        public int getWidth() {
            return width;
        }
        
        public void setWidth(int width) {
            this.width = width;
        }
        
        public int getHeight() {
            return height;
        }
        
        public void setHeight(int height) {
            this.height = height;
        }
        
        public long getSize() {
            return size;
        }
        
        public void setSize(long size) {
            this.size = size;
        }
        
        public String getImageHexType() {
            return imageHexType;
        }
        
        public void setImageHexType(String imageHexType) {
            this.imageHexType = imageHexType;
        }
    }
} 