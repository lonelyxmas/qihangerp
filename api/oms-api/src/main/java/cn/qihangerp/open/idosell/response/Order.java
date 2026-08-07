package cn.qihangerp.open.idosell.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class Order {

    @JsonProperty("clientResult")
    private ClientResultDTO clientResult;
    @JsonProperty("errors")
    private List<?> errors;
    @JsonProperty("orderBridgeNote")
    private String orderBridgeNote;
    @JsonProperty("orderDetails")
    private OrderDetailsDTO orderDetails;
    @JsonProperty("orderId")
    private String orderId;
    @JsonProperty("orderSerialNumber")
    private Long orderSerialNumber;
    @JsonProperty("orderType")
    private String orderType;

    @NoArgsConstructor
    @Data
    public static class ClientResultDTO {
        @JsonProperty("clientAccount")
        private ClientAccountDTO clientAccount;
        @JsonProperty("clientBillingAddress")
        private ClientBillingAddressDTO clientBillingAddress;
        @JsonProperty("clientDeliveryAddress")
        private ClientDeliveryAddressDTO clientDeliveryAddress;
        @JsonProperty("endClientAccount")
        private EndClientAccountDTO endClientAccount;

        @NoArgsConstructor
        @Data
        public static class ClientAccountDTO {
            @JsonProperty("clientCodeExternal")
            private String clientCodeExternal;
            @JsonProperty("clientEmail")
            private String clientEmail;
            @JsonProperty("clientId")
            private Long clientId;
            @JsonProperty("clientLogin")
            private String clientLogin;
            @JsonProperty("clientPhone1")
            private String clientPhone1;
            @JsonProperty("clientPhone2")
            private String clientPhone2;
        }

        @NoArgsConstructor
        @Data
        public static class ClientBillingAddressDTO {
            @JsonProperty("clientCity")
            private String clientCity;
            @JsonProperty("clientCountryId")
            private String clientCountryId;
            @JsonProperty("clientCountryName")
            private String clientCountryName;
            @JsonProperty("clientFirm")
            private String clientFirm;
            @JsonProperty("clientFirstName")
            private String clientFirstName;
            @JsonProperty("clientLastName")
            private String clientLastName;
            @JsonProperty("clientNip")
            private String clientNip;
            @JsonProperty("clientPhone1")
            private String clientPhone1;
            @JsonProperty("clientPhone2")
            private String clientPhone2;
            @JsonProperty("clientProvince")
            private String clientProvince;
            @JsonProperty("clientProvinceId")
            private String clientProvinceId;
            @JsonProperty("clientStreet")
            private String clientStreet;
            @JsonProperty("clientZipCode")
            private String clientZipCode;
        }

        @NoArgsConstructor
        @Data
        public static class ClientDeliveryAddressDTO {
            @JsonProperty("clientDeliveryAddressCity")
            private String clientDeliveryAddressCity;
            @JsonProperty("clientDeliveryAddressCountry")
            private String clientDeliveryAddressCountry;
            @JsonProperty("clientDeliveryAddressCountryId")
            private String clientDeliveryAddressCountryId;
            @JsonProperty("clientDeliveryAddressFirm")
            private String clientDeliveryAddressFirm;
            @JsonProperty("clientDeliveryAddressFirstName")
            private String clientDeliveryAddressFirstName;
            @JsonProperty("clientDeliveryAddressId")
            private String clientDeliveryAddressId;
            @JsonProperty("clientDeliveryAddressLastName")
            private String clientDeliveryAddressLastName;
            @JsonProperty("clientDeliveryAddressPhone1")
            private String clientDeliveryAddressPhone1;
            @JsonProperty("clientDeliveryAddressPhone2")
            private String clientDeliveryAddressPhone2;
            @JsonProperty("clientDeliveryAddressPickupPointInternalId")
            private Long clientDeliveryAddressPickupPointInternalId;
            @JsonProperty("clientDeliveryAddressProvince")
            private String clientDeliveryAddressProvince;
            @JsonProperty("clientDeliveryAddressProvinceId")
            private String clientDeliveryAddressProvinceId;
            @JsonProperty("clientDeliveryAddressStreet")
            private String clientDeliveryAddressStreet;
            @JsonProperty("clientDeliveryAddressType")
            private String clientDeliveryAddressType;
            @JsonProperty("clientDeliveryAddressZipCode")
            private String clientDeliveryAddressZipCode;
        }

        @NoArgsConstructor
        @Data
        public static class EndClientAccountDTO {
            @JsonProperty("clientCodeExternal")
            private String clientCodeExternal;
            @JsonProperty("clientEmail")
            private String clientEmail;
            @JsonProperty("clientId")
            private Integer clientId;
            @JsonProperty("clientLogin")
            private String clientLogin;
            @JsonProperty("clientPhone1")
            private String clientPhone1;
            @JsonProperty("clientPhone2")
            private String clientPhone2;
        }
    }

    @NoArgsConstructor
    @Data
    public static class OrderDetailsDTO {
        @JsonProperty("apiFlag")
        private String apiFlag;
        @JsonProperty("auctionInfo")
        private AuctionInfoDTO auctionInfo;
        @JsonProperty("clientDeliveryAddressId")
        private Integer clientDeliveryAddressId;
        @JsonProperty("clientNoteToCourier")
        private String clientNoteToCourier;
        @JsonProperty("clientNoteToOrder")
        private String clientNoteToOrder;
        @JsonProperty("clientRequestInvoice")
        private String clientRequestInvoice;
        @JsonProperty("dispatch")
        private DispatchDTO dispatch;
        @JsonProperty("dropshippingOrderStatus")
        private String dropshippingOrderStatus;
        @JsonProperty("orderAddDate")
        private String orderAddDate;
        @JsonProperty("orderChangeDate")
        private String orderChangeDate;
        @JsonProperty("orderConfirmation")
        private String orderConfirmation;
        @JsonProperty("orderDispatchDate")
        private Long orderDispatchDate;
        @JsonProperty("orderNote")
        private String orderNote;
        @JsonProperty("orderOperatorLogin")
        private String orderOperatorLogin;
        @JsonProperty("orderPrepareTime")
        private Integer orderPrepareTime;
        @JsonProperty("orderSourceResults")
        private OrderSourceResultsDTO orderSourceResults;
        @JsonProperty("orderStatus")
        private String orderStatus;
        @JsonProperty("payments")
        private PaymentsDTO payments;
        @JsonProperty("prepaids")
        private List<?> prepaids;
        @JsonProperty("productRemovedInStock")
        private String productRemovedInStock;
        @JsonProperty("productsResults")
        private List<ProductsResultsDTO> productsResults;
        @JsonProperty("purchaseDate")
        private String purchaseDate;
        @JsonProperty("receivedDate")
        private String receivedDate;
        @JsonProperty("stockId")
        private Integer stockId;
        @JsonProperty("subscriptionId")
        private Integer subscriptionId;

        @NoArgsConstructor
        @Data
        public static class AuctionInfoDTO {
        }

        @NoArgsConstructor
        @Data
        public static class DispatchDTO {
            @JsonProperty("courierId")
            private Long courierId;
            @JsonProperty("courierName")
            private String courierName;
            @JsonProperty("courierWebserviceOnly")
            private Boolean courierWebserviceOnly;
            @JsonProperty("deliveryDate")
            private String deliveryDate;
            @JsonProperty("deliveryDateAdditional")
            private String deliveryDateAdditional;
            @JsonProperty("deliveryPackageId")
            private String deliveryPackageId;
            @JsonProperty("deliveryWeight")
            private Integer deliveryWeight;
            @JsonProperty("estimatedDeliveryDate")
            private String estimatedDeliveryDate;
        }

        @NoArgsConstructor
        @Data
        public static class OrderSourceResultsDTO {
            @JsonProperty("auctionsServiceName")
            private String auctionsServiceName;
            @JsonProperty("orderSourceDetails")
            private OrderSourceDetailsDTO orderSourceDetails;
            @JsonProperty("orderSourceType")
            private String orderSourceType;
            @JsonProperty("preorderSourcesDetails")
            private List<?> preorderSourcesDetails;
            @JsonProperty("shopId")
            private Long shopId;

            @NoArgsConstructor
            @Data
            public static class OrderSourceDetailsDTO {
                @JsonProperty("entryProductIdBeforeOrder")
                private Long entryProductIdBeforeOrder;
                @JsonProperty("fresh")
                private String fresh;
                @JsonProperty("fulfillment")
                private String fulfillment;
                @JsonProperty("orderSourceId")
                private Long orderSourceId;
                @JsonProperty("orderSourceName")
                private String orderSourceName;
                @JsonProperty("orderSourceType")
                private String orderSourceType;
                @JsonProperty("orderSourceTypeId")
                private Integer orderSourceTypeId;
                @JsonProperty("sourcePageUrl")
                private String sourcePageUrl;
            }
        }

        @NoArgsConstructor
        @Data
        public static class PaymentsDTO {
            @JsonProperty("orderBaseCurrency")
            private OrderBaseCurrencyDTO orderBaseCurrency;
            @JsonProperty("orderCurrency")
            private OrderCurrencyDTO orderCurrency;
            @JsonProperty("orderPaymentDays")
            private Integer orderPaymentDays;
            @JsonProperty("orderPaymentType")
            private String orderPaymentType;
            @JsonProperty("orderRebatePercent")
            private Integer orderRebatePercent;
            @JsonProperty("orderVatExists")
            private String orderVatExists;
            @JsonProperty("orderWorthCalculateType")
            private String orderWorthCalculateType;

            @NoArgsConstructor
            @Data
            public static class OrderBaseCurrencyDTO {
                @JsonProperty("billingCurrency")
                private String billingCurrency;
                @JsonProperty("orderDeliveryCost")
                private Integer orderDeliveryCost;
                @JsonProperty("orderDeliveryVat")
                private Integer orderDeliveryVat;
                @JsonProperty("orderInsuranceCost")
                private Integer orderInsuranceCost;
                @JsonProperty("orderInsuranceVat")
                private Integer orderInsuranceVat;
                @JsonProperty("orderPayformCost")
                private Integer orderPayformCost;
                @JsonProperty("orderPayformVat")
                private Integer orderPayformVat;
                @JsonProperty("orderProductsCost")
                private Integer orderProductsCost;
            }

            @NoArgsConstructor
            @Data
            public static class OrderCurrencyDTO {
                @JsonProperty("billingCurrencyRate")
                private Double billingCurrencyRate;
                @JsonProperty("currencyId")
                private String currencyId;
                @JsonProperty("orderCurrencyValue")
                private Integer orderCurrencyValue;
                @JsonProperty("orderDeliveryCost")
                private Integer orderDeliveryCost;
                @JsonProperty("orderInsuranceCost")
                private Integer orderInsuranceCost;
                @JsonProperty("orderPayformCost")
                private Integer orderPayformCost;
                @JsonProperty("orderProductsCost")
                private Integer orderProductsCost;
            }
        }

        @NoArgsConstructor
        @Data
        public static class ProductsResultsDTO {
            @JsonProperty("basketPosition")
            private Integer basketPosition;
            @JsonProperty("bundleId")
            private Long bundleId;
            @JsonProperty("orderSalesMode")
            private String orderSalesMode;
            @JsonProperty("productCode")
            private String productCode;
            @JsonProperty("productId")
            private Long productId;
            @JsonProperty("productName")
            private String productName;
            @JsonProperty("productOrderAdditional")
            private String productOrderAdditional;
            @JsonProperty("productOrderPrice")
            private Integer productOrderPrice;
            @JsonProperty("productOrderPriceBaseCurrency")
            private Integer productOrderPriceBaseCurrency;
            @JsonProperty("productOrderPriceNet")
            private Integer productOrderPriceNet;
            @JsonProperty("productOrderPriceNetBaseCurrency")
            private Integer productOrderPriceNetBaseCurrency;
            @JsonProperty("productPanelPrice")
            private Integer productPanelPrice;
            @JsonProperty("productPanelPriceNet")
            private Integer productPanelPriceNet;
            @JsonProperty("productPriceLog")
            private String productPriceLog;
            @JsonProperty("productQuantity")
            private Integer productQuantity;
            @JsonProperty("productSizeCodeExternal")
            private String productSizeCodeExternal;
            @JsonProperty("productVat")
            private Integer productVat;
            @JsonProperty("productWeight")
            private Integer productWeight;
            @JsonProperty("remarksToProduct")
            private String remarksToProduct;
            @JsonProperty("sizeId")
            private String sizeId;
            @JsonProperty("sizePanelName")
            private String sizePanelName;
            @JsonProperty("stockId")
            private Long stockId;
            @JsonProperty("versionName")
            private String versionName;
        }
    }
}
