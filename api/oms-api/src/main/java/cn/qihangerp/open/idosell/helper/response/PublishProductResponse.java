package cn.qihangerp.open.idosell.helper.response;

import java.util.List;

import lombok.Data;

/**
 * IdoSell API 发布产品响应
 *
 * @author wangmingxin
 * @version : ProductsResultResponse.java, v 0.1 2025/03/16 22:06 wangmingxin Exp $
 */
@Data
public class PublishProductResponse {

    /**
     * 结果
     */
    private Results results;

    @Data
    public static class Results {

        /**
         * 产品结果列表，因网关调用返回的产品列表
         */
        private List<ProductResult> productsResults;
    }

    @Data
    public static class ProductResult {

        /**
         * 产品 IAI 代码
         */
        private String productId;

        /**
         * 尺寸标识符
         */
        private String sizeId;

        /**
         * 尺寸的外部产品系统代码
         */
        private String productSizeCodeExternal;

        /**
         * 包含错误详细信息的元素列表
         */
        private List<Fault> faults;

        /**
         * 包含执行操作详细信息的对象列表
         */
        private List<ProductResultDetail> productResultDetails;
    }

    @Data
    public static class Fault {

        /**
         * 错误代码
         */
        private String faultCode;

        /**
         * 错误描述
         */
        private String faultString;
    }

    @Data
    public static class ProductResultDetail {

        /**
         * 库存 ID
         */
        private String stockId;

        /**
         * 产品库存数量
         */
        private String productSizeQuantity;

        /**
         * 商店 ID
         */
        private String shopId;

        /**
         * 尺寸标识符
         */
        private String sizeId;

        /**
         * 操作类型
         */
        private String operationType;

        /**
         * 包含错误详细信息的元素
         */
        private Fault fault;

        /**
         * 现有代码列表
         */
        private List<ExistingCode> existingCodes;
    }

    @Data
    public static class ExistingCode {

        /**
         * 产品 IAI 代码
         */
        private String productId;

        /**
         * 尺寸标识符
         */
        private String sizeId;

        /**
         * 尺寸的外部产品系统代码
         */
        private String productSizeCodeExternal;

        /**
         * 生产商代码
         */
        private String productProducerCode;
    }
}