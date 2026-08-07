package cn.qihangerp.open.idosell.helper.request;

import java.util.List;

import lombok.Data;

/**
 * IdoSell API 发布产品请求实体类
 *
 * @author wangmingxin
 * @version : ProductsRequest.java, v 0.1 2025/03/16 22:17 wangmingxin Exp $
 */
@Data
public class PublishProductRequest {
    /**
     * 参数对象
     */
    private Params params;

    @Data
    public static class Params {
        /**
         * 设置
         */
        private Settings settings;
        /**
         * 产品列表
         */
        private List<Product> products;
    }

    @Data
    public static class Settings {
        /**
         * 价格格式。当前未使用
         */
        private String settingPriceFormat;
        /**
         * 是否允许添加新类别
         * 允许的值：
         * "n" - 不允许添加新类别（默认值）
         * "y" - 可以添加新类别
         */
        private String settingAddingCategoryAllowed;
        /**
         * 是否允许添加新尺寸
         * 允许的值：
         * "n" - 不允许添加新尺寸（默认值）
         * "y" - 可以添加新尺寸
         */
        private String settingAddingSizeAllowed;
        /**
         * 是否允许添加新生产商
         * 允许的值：
         * "n" - 不允许添加新生产商（默认值）
         * "y" - 可以添加新生产商
         */
        private String settingAddingProducerAllowed;
        /**
         * 是否允许添加新系列
         * 允许的值：
         * "n" - 不允许添加新系列（默认值）
         * "y" - 可以添加新系列
         */
        private String settingAddingSeriesAllowed;
        /**
         * 默认类别
         */
        private DefaultCategory settingDefaultCategory;
        /**
         * 默认尺寸组
         */
        private DefaultSizesGroup settingDefaultSizesGroup;
        /**
         * 是否可以设置默认商店可见性
         */
        private String settingsAddingDefaultShopMaskAllowed;
        /**
         * 是否可以根据配置设置默认商店可见性
         */
        private String settingsAddingManuallySelectedShopMaskAllowed;
        /**
         * 图标和照片设置
         */
        private PicturesSettings picturesSettings;
    }

    @Data
    public static class DefaultCategory {
        /**
         * 类别 ID
         */
        private Integer categoryId;
        /**
         * 类别名称
         */
        private String categoryName;
    }

    @Data
    public static class DefaultSizesGroup {
        /**
         * 尺寸组 ID
         */
        private Integer sizesGroupId;
        /**
         * 尺寸组名称
         */
        private String sizesGroupName;
    }

    @Data
    public static class PicturesSettings {
        /**
         * 照片 URL 的初始部分
         */
        private String picturesSettingInitialUrlPart;
        /**
         * 照片添加方法
         * 允许的值：
         * "base64" - 使用 base64 编码添加照片
         * "url" - 作为 URL 添加照片
         */
        private String picturesSettingInputType;
        /**
         * 添加产品照片的方法
         * 允许的值：
         * "n" - 从第一个空位上传照片
         * "y" - 从第一个位置上传照片
         */
        private String picturesSettingOverwrite;
        /**
         * 是否允许缩放照片
         * 允许的值：
         * "n" - 不允许缩放
         * "y" - 允许缩放
         */
        private String picturesSettingScaling;
    }

    @Data
    public static class Product {
        /**
         * 产品 IAI 代码
         */
        private Integer productId;
        /**
         * 外部产品系统尺寸代码
         */
        private String productSizeCodeExternal;
        /**
         * 外部产品系统代码
         */
        private String productDisplayedCode;
        /**
         * PKWiU [PCPandS]
         */
        private String productTaxCode;
        /**
         * 包装中的物品数量
         */
        private Integer productInWrapper;
        /**
         * 零售价
         */
        private Float productSellByRetail;
        /**
         * 批发价
         */
        private Float productSellByWholesale;
        /**
         * IdoSell 类别 ID
         */
        private Integer categoryIdoSellId;
        /**
         * IdoSell 类别路径名
         */
        private String categoryIdoSellPath;
        /**
         * 类别 ID
         */
        private Integer categoryId;
        /**
         * 类别名称
         */
        private String categoryName;
        /**
         * 品牌 ID
         */
        private Integer producerId;
        /**
         * 品牌名称
         */
        private String producerName;
        /**
         * CN/TARIC 代码
         */
        private String cnTaricCode;
        /**
         * 原产国，ISO 3166-1 A2 标准国家代码
         */
        private String countryOfOrigin;
        /**
         * 产品计量单位 ID
         */
        private Integer unitId;
        /**
         * 产品所属系列 ID
         */
        private Integer seriesId;
        /**
         * 产品所属系列名称，面板中可见
         */
        private String seriesPanelName;
        /**
         * 尺寸组 ID
         */
        private Integer sizesGroupId;
        /**
         * 价格编辑模式
         */
        private String priceChangeMode;
        /**
         * JavaScript 价格计算公式
         */
        private PriceFormula priceFormula;
        /**
         * 产品在 POS 中的可用性
         */
        private String productEnableInPos;
        /**
         * 所需预付款百分比
         */
        private Float productAdvancePrice;
        /**
         * 注释
         */
        private String productNote;
        /**
         * 产品积分值
         */
        private Float productProfitPoints;
        /**
         * 重量
         */
        private Integer productWeight;
        /**
         * 产品可见性
         */
        private String productInVisible;
        /**
         * 缺货时仍可见
         */
        private String productInPersistent;
        /**
         * 商店 ID 的位掩码
         */
        private Integer shopsMask;
        /**
         * 复杂评分
         */
        private Integer productComplexNotes;
        /**
         * 产品在价格比较和市场中的可见性
         */
        private String productInExportToPriceComparisonSites;
        /**
         * 产品在Amazon Marketplace中的可见性
         */
        private String productInExportToAmazonMarketplace;
        /**
         * 可用性配置文件 ID
         */
        private Integer availableProfile;
        /**
         * 折扣配置文件 ID
         */
        private Integer productRebate;
        /**
         * 产品保修 ID
         */
        private Integer warrantyId;
        /**
         * 优先级，允许的值从 1 到 10
         */
        private Integer productPriority;
        /**
         * 产品图标详细信息
         */
        private String productIcon;
        /**
         * 水印 ID
         */
        private Integer productWatermarkId;
        /**
         * 水印链接
         */
        private String productWatermarkUrl;
        /**
         * 产品图片列表
         */
        private List<String> productPictures;
        /**
         * 图片描述列表
         */
        private List<String> productDescriptionPictures;
        /**
         * 促销价格详细信息
         */
        private ProductPromotion productPromotion;
        /**
         * 商店折扣详细信息
         */
        private ProductDiscount productDiscount;
        /**
         * 商店中的特色产品详细信息
         */
        private ProductDistinguished productDistinguished;
        /**
         * 商店中的特殊产品详细信息
         */
        private ProductSpecial productSpecial;
        /**
         * 推荐产品列表
         */
        private List<AssociatedProduct> associatedProducts;
        /**
         * 产品可用的尺寸数据
         */
        private List<ProductSize> productSizes;
        /**
         * 与指定商店相关的产品属性
         */
        private List<ProductShopAttribute> productShopsAttributes;
        /**
         * 产品名称
         */
        private ProductNames productNames;
        /**
         * 产品描述
         */
        private ProductDescriptions productDescriptions;
        /**
         * 长产品描述
         */
        private ProductLongDescriptions productLongDescriptions;
        /**
         * 产品拍卖服务数据
         */
        private List<ProductAuctionDescriptionData> productAuctionDescriptionsData;
        /**
         * 产品元标题
         */
        private ProductMetaTitles productMetaTitles;
        /**
         * 产品元描述
         */
        private ProductMetaDescriptions productMetaDescriptions;
        /**
         * 产品URL地址
         */
        private ProductUrl productUrl;
        /**
         * 产品组（变体）数据
         */
        private ProductVersion productVersion;
        /**
         * 货币ID
         */
        private String currencyId;
        /**
         * 供应商ID
         */
        private Integer delivererId;
        /**
         * 产品参数区分编辑模式
         */
        private String productParametersDistinctionChangeMode;
        /**
         * 产品从生产商到商店的交货时间
         */
        private ProductDeliveryTime productDeliveryTime;
        /**
         * 产品在购物车中是否合并为一个订单
         */
        private String productSumInBasket;
        /**
         * 配送设置
         */
        private DispatchSettings dispatchSettings;
        /**
         * 标准单位设置
         */
        private StandardUnit standardUnit;
        /**
         * 每个订单的最小产品数量
         */
        private MinQuantityPerOrder minQuantityPerOrder;
        /**
         * 尺寸和总体重量
         */
        private ProductDimensions productDimensions;
        /**
         * 负责生产者代码
         */
        private String responsibleProducerCode;
        /**
         * 负责人员代码
         */
        private String responsiblePersonCode;

        @Data
        public static class ProductPromotion {
            /**
             * 是否激活促销
             * "y" - 激活促销,
             * "n" - 不激活促销
             */
            private String promoteInEnabled;
            /**
             * 划线价
             */
            private Float promoteItemNormalPrice;
            /**
             * 批发划线价
             */
            private Float promoteItemWholesaleNormalPrice;
            /**
             * 促销结束日期
             */
            private String promoteItemEndingDate;
        }

        @Data
        public static class ProductDiscount {
            /**
             * 是否激活折扣
             * "y" - 激活折扣,
             * "n" - 不激活折扣
             */
            private String promoteInEnabled;
            /**
             * 划线价
             */
            private Float promoteItemNormalPrice;
            /**
             * 批发划线价
             */
            private Float promoteItemWholesaleNormalPrice;
            /**
             * 折扣结束日期
             */
            private String promoteItemEndingDate;
        }


        @Data
        public static class PriceFormula {
            /**
             * 零售价
             */
            private Float productRetailPrice;
            /**
             * 批发价
             */
            private Float productWholesalePrice;
            /**
             * 最低价
             */
            private Float productMinimalPrice;
            /**
             * 自动计算价格
             */
            private Float productAutomaticCalculationPrice;
            /**
             * POS价格
             */
            private Float productPosPrice;
            /**
             * 增值税值
             */
            private Float productVat;
            /**
             * 产品是否免征增值税
             * "y" - 是,
             * "n" - 否
             */
            private String productVatFree;
        }

        @Data
        public static class ProductDistinguished {
            /**
             * 是否激活特色
             * "y" - 激活特色,
             * "n" - 不激活特色
             */
            private String promoteInEnabled;
            /**
             * 划线价
             */
            private Float promoteItemNormalPrice;
            /**
             * 批发划线价
             */
            private Float promoteItemWholesaleNormalPrice;
            /**
             * 特色结束日期
             */
            private String promoteItemEndingDate;
        }

        @Data
        public static class ProductSpecial {
            /**
             * 是否激活特别产品
             * "y" - 激活特别产品,
             * "n" - 不激活特别产品
             */
            private String promoteInEnabled;
            /**
             * 划线价
             */
            private Float promoteItemNormalPrice;
            /**
             * 批发划线价
             */
            private Float promoteItemWholesaleNormalPrice;
            /**
             * 特别产品结束日期
             */
            private String promoteItemEndingDate;
        }
    }

    @Data
    public static class ProductPriceComparisonSitePrice {
        /**
         * 价格比较网站 ID
         */
        private Integer priceComparisonSiteId;
        /**
         * 商店中价格比较网站的价格
         */
        private Float productPriceComparisonSitePrice;
    }


    @Data
    public static class AssociatedProduct {
        /**
         * 推荐产品 ID
         */
        private Integer associatedProductId;
        /**
         * 推荐产品名称
         */
        private String associatedProductName;
        /**
         * 推荐产品代码
         */
        private String associatedProductCode;
    }

    @Data
    public static class ProductSize {
        /**
         * 尺寸标识符
         */
        private String sizeId;
        /**
         * 尺寸名称
         */
        private String sizePanelName;
        /**
         * 重量
         */
        private Integer productWeight;
        /**
         * 净重
         */
        private Integer productWeightNet;
        /**
         * 零售价
         */
        private Float productRetailPrice;
        /**
         * 批发价
         */
        private Float productWholesalePrice;
        /**
         * 最低价
         */
        private Float productMinimalPrice;
        /**
         * 自动计算价格
         */
        private Float productAutomaticCalculationPrice;
        /**
         * POS价格
         */
        private Float productPosPrice;
    }

    @Data
    public static class ProductShopAttribute {
        /**
         * 商店 ID
         */
        private Integer shopId;
        /**
         * 商店中价格比较网站的价格
         */
        private List<ProductPriceComparisonSitePrice> productShopPriceComparisonSitesPrices;
    }

    @Data
    public static class ProductNames {
        /**
         * 产品名称的语言数据
         */
        private List<ProductNamesLangData> productNamesLangData;
    }

    @Data
    public static class ProductNamesLangData {
        /**
         * 语言 ID
         */
        private String langId;
        /**
         * 产品名称
         */
        private String productName;
    }

    @Data
    public static class ProductDescriptions {
        /**
         * 产品描述的语言数据
         */
        private List<ProductDescriptionsLangData> productDescriptionsLangData;
    }

    @Data
    public static class ProductDescriptionsLangData {
        /**
         * 语言 ID
         */
        private String langId;
        /**
         * 产品短描述
         */
        private String productDescription;
    }

    @Data
    public static class ProductLongDescriptions {
        /**
         * 产品长描述的语言数据
         */
        private List<ProductLongDescriptionsLangData> productLongDescriptionsLangData;
    }

    @Data
    public static class ProductLongDescriptionsLangData {
        /**
         * 语言 ID
         */
        private String langId;
        /**
         * 产品长描述
         */
        private String productLongDescription;
    }

    @Data
    public static class ProductAuctionDescriptionData {
        /**
         * 拍卖系统 ID
         */
        private String productAuctionId;
        /**
         * 拍卖网站 ID
         */
        private String productAuctionSiteId;
        /**
         * 拍卖服务的产品名称
         */
        private String productAuctionName;
        /**
         * 拍卖服务的副标题
         */
        private String productAuctionAdditionalName;
        /**
         * 市场的产品描述
         */
        private String productAuctionDescription;
    }

    @Data
    public static class ProductMetaTitles {
        /**
         * 产品元标题的语言数据
         */
        private List<ProductMetaTitlesLangData> productMetaTitlesLangData;
    }

    @Data
    public static class ProductMetaTitlesLangData {
        /**
         * 语言 ID
         */
        private String langId;
        /**
         * 产品元标题
         */
        private String productMetaTitle;
    }

    @Data
    public static class ProductMetaDescriptions {
        /**
         * 产品元描述的语言数据
         */
        private List<ProductMetaDescriptionsLangData> productMetaDescriptionsLangData;
    }

    @Data
    public static class ProductMetaDescriptionsLangData {
        /**
         * 语言 ID
         */
        private String langId;
        /**
         * 产品元描述
         */
        private String productMetaDescription;
    }

    @Data
    public static class ProductUrl {
        /**
         * 产品 URL 的语言数据
         */
        private List<ProductUrlsLangData> productUrlsLangData;
    }

    @Data
    public static class ProductUrlsLangData {
        /**
         * 商店 ID
         */
        private Integer shopId;
        /**
         * 语言 ID
         */
        private String langId;
        /**
         * URL 地址
         */
        private String url;
    }

    @Data
    public static class ProductVersion {
        /**
         * 主产品（变体）ID
         */
        private Integer versionParentId;
        /**
         * 产品在组内的顺序
         */
        private Integer versionPriority;
        /**
         * 组内项目（变体）的设置
         */
        private VersionSettings versionSettings;
    }

    @Data
    public static class VersionSettings {
        /**
         * 参数值名称
         */
        private VersionNames versionNames;
        /**
         * 参数组名称
         */
        private VersionGroupNames versionGroupNames;
    }

    @Data
    public static class VersionNames {
        /**
         * 参数值名称的语言数据
         */
        private List<VersionNamesLangData> versionNamesLangData;

        @Data
        public static class VersionNamesLangData {
            /**
             * 语言 ID
             */
            private String langId;
            /**
             * 参数值名称，例如橙色、绿色、红色
             */
            private String versionName;
        }
    }

    @Data
    public static class VersionGroupNames {
        /**
         * 参数名称的语言数据
         */
        private List<VersionGroupNamesLangData> versionGroupNamesLangData;

        @Data
        public static class VersionGroupNamesLangData {
            /**
             * 语言 ID
             */
            private String langId;
            /**
             * 参数名称，例如颜色、宽度
             */
            private String versionGroupName;
        }
    }

    @Data
    public static class ProductDeliveryTime {
        /**
         * 操作类型
         * "product" - 设置自己的产品交货时间,
         * "deliverer" - 设置与供应商相同的产品交货时间
         */
        private String productDeliveryTimeChangeMode;
        /**
         * 从供应商到商店的交货时间
         */
        private Integer productDeliveryTimeValue;
    }

    @Data
    public static class DispatchSettings {
        /**
         * 是否启用
         */
        private Boolean enabled;
        /**
         * 配送设置
         */
        private ShippingSettings shippingSettings;
        /**
         * 免费配送设置
         */
        private FreeShippingSettings freeShippingSettings;
        /**
         * 退货和投诉设置
         */
        private ReturnProductSettings returnProductSettings;
    }

    @Data
    public static class ShippingSettings {
        /**
         * 禁用货到付款
         */
        private Boolean codDisabled;
        /**
         * 仅限个人收集
         */
        private Boolean dvpOnly;
        /**
         * 超大产品
         */
        private Boolean atypicalSize;
        /**
         * 需要保险
         */
        private Boolean insuranceOnly;
        /**
         * 排除微笑服务
         */
        private Boolean excludeSmileService;
        /**
         * 不可使用的快递服务列表
         */
        private List<Integer> disallowedCouriers;
    }

    @Data
    public static class FreeShippingSettings {
        /**
         * 编辑模式
         */
        private String mode;
        /**
         * 可用支付方式
         */
        private AvailablePaymentForms availablePaymentForms;
        /**
         * 免费配送的快递服务列表
         */
        private List<Integer> availableCouriers;
        /**
         * 免费配送的地区列表
         */
        private List<Integer> availableRegions;
    }

    @Data
    public static class AvailablePaymentForms {
        /**
         * 预付
         */
        private Boolean prepaid;
        /**
         * 货到付款
         */
        private Boolean cashOnDelivery;
        /**
         * 商业信用
         */
        private Boolean tradeCredit;
    }

    @Data
    public static class ReturnProductSettings {
        // Define fields for return and complaint settings
    }

    @Data
    public static class StandardUnit {
        /**
         * 特殊上下文对应的标准单位
         */
        private String contextValue;
        /**
         * 产品的总长度/体积/面积/重量
         */
        private Float standardUnitValue;
        /**
         * 每单位价格转换器
         */
        private String converterUnitValue;
    }

    @Data
    public static class MinQuantityPerOrder {
        /**
         * 零售订单的最小产品数量
         */
        private Float minQuantityPerOrderRetail;
        /**
         * 批发订单的最小产品数量
         */
        private Float minQuantityPerOrderWholesale;
    }

    @Data
    public static class ProductDimensions {
        /**
         * 产品宽度（厘米）
         */
        private Float productWidth;
        /**
         * 产品高度（厘米）
         */
        private Float productHeight;
        /**
         * 产品长度（厘米）
         */
        private Float productLength;
    }
}

