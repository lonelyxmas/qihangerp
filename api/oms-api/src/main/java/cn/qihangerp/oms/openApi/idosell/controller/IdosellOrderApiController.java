package cn.qihangerp.oms.openApi.idosell.controller;

import cn.qihangerp.oms.openApi.PullRequest;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.enums.HttpStatus;
import cn.qihangerp.common.mq.MqMessage;
import cn.qihangerp.common.mq.MqType;
import cn.qihangerp.common.mq.MqUtils;
import cn.qihangerp.domain.OShop;
import cn.qihangerp.domain.OShopPullLasttime;
import cn.qihangerp.domain.OShopPullLogs;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellOrder;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellOrderItem;
import cn.qihangerp.open.idosell.helper.IdosellOrderApiHelper;
import cn.qihangerp.open.idosell.response.Order;
import cn.qihangerp.open.idosell.service.OmsIdosellOrderService;
import cn.qihangerp.oms.service.OShopPullLasttimeService;
import cn.qihangerp.oms.service.OShopPullLogsService;
import cn.qihangerp.oms.service.OShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RequestMapping("/api/open-api/idosell/order")
@RestController
@RequiredArgsConstructor
public class IdosellOrderApiController extends BaseController {
    private final OShopService shopService;
    private final IdosellOrderApiHelper idosellOrderApiHelper;
    private final MqUtils mqUtils;
    private final OShopPullLogsService pullLogsService;
    private final OShopPullLasttimeService pullLasttimeService;
    private final OmsIdosellOrderService orderService;
    /**
     * 拉取商品列表（包含sku）
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pull_order", method = RequestMethod.POST)
    public AjaxResult pullOrderList(@RequestBody PullRequest params) throws Exception {
        if (params.getShopId() == null || params.getShopId() <= 0) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:missing parameter shopId");
        }
        OShop shop = shopService.getById(params.getShopId());
        if(shop == null)return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:shop not exist");
        else if(!StringUtils.hasText(shop.getApiRequestUrl())) return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:missing api request url");
        else if(!StringUtils.hasText(shop.getAppKey())) return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:missing api key");
        Date currDateTime = new Date();
        Long currTimeMillis = System.currentTimeMillis();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 获取最后更新时间
        LocalDateTime startTime = null;
        LocalDateTime  endTime = null;
        OShopPullLasttime lasttime = pullLasttimeService.getLasttimeByShop(params.getShopId(), "ORDER");
        if(lasttime == null){
            endTime = LocalDateTime.now();
            startTime = endTime.minusDays(1);
        }else {
            startTime = lasttime.getLasttime().minusHours(1);//取上次结束一个小时前
            Duration duration = Duration.between(startTime, LocalDateTime.now());
            long hours = duration.toHours();
            if (hours > 24) {
                // 大于24小时，只取24小时
                endTime = startTime.plusHours(24);
            } else {
                endTime = LocalDateTime.now();
            }
//            endTime = startTime.plusDays(1);//取24小时
//            if(endTime.isAfter(LocalDateTime.now())){
//                endTime = LocalDateTime.now();
//            }
        }
//        String startTimeStr = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        String endTimeStr = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String pullParams = "{startTime:"+startTime.format(formatter)+",endTime:"+endTime.format(formatter)+"}";
        log.info("===========主动拉取idosell订单，时间段====="+pullParams);


        var resultVo = idosellOrderApiHelper.getOrderList(shop.getApiRequestUrl(), shop.getAppKey(), 0, 20,startTime,endTime);
        if(resultVo.getCode() !=0 ){
            OShopPullLogs logs = new OShopPullLogs();
            logs.setShopId(params.getShopId());
            logs.setShopType(EnumShopType.IDOSELL.getIndex());
            logs.setPullType("ORDER");
            logs.setPullWay("主动拉取订单");
            logs.setPullParams(pullParams);
            logs.setPullResult(resultVo.getMsg());
            logs.setPullTime(currDateTime);
            logs.setDuration(System.currentTimeMillis() - currTimeMillis);
            pullLogsService.save(logs);
            return AjaxResult.error("接口拉取错误："+resultVo.getMsg());
        }
        int insertSuccess = 0;//新增成功的订单
        int totalError = 0;
        int hasExistOrder = 0;//已存在的订单数

        //循环插入订单数据到数据库
        for (var order : resultVo.getList()) {
            // 创建订单对象并赋值
            OmsIdosellOrder orderObj = new OmsIdosellOrder();

            // 基本信息
            orderObj.setOrderId(order.getOrderId());                          // endrodireka2-1
            orderObj.setOrderSerialNumber(order.getOrderSerialNumber());      // 1374398
            orderObj.setOrderType(order.getOrderType());                      // n

            // 客户账户信息
            orderObj.setClientId(order.getClientResult().getClientAccount().getClientId());                    // 957908
            orderObj.setClientLogin(order.getClientResult().getClientAccount().getClientLogin());              // endrodireka2
            orderObj.setClientEmail(order.getClientResult().getClientAccount().getClientEmail());              // endrodireka@gmail.com
            orderObj.setClientPhone1(order.getClientResult().getClientAccount().getClientPhone1());            // +36 30 940 6113
            orderObj.setClientPhone2(order.getClientResult().getClientAccount().getClientPhone2());            // ""
            orderObj.setClientCodeExternal(order.getClientResult().getClientAccount().getClientCodeExternal()); // ""

            // 账单地址信息
            orderObj.setBillingFirstName(order.getClientResult().getClientBillingAddress().getClientFirstName());   // Endrődi
            orderObj.setBillingLastName(order.getClientResult().getClientBillingAddress().getClientLastName());     // Réka
            orderObj.setBillingFirm(order.getClientResult().getClientBillingAddress().getClientFirm());            // ""
            orderObj.setBillingStreet(order.getClientResult().getClientBillingAddress().getClientStreet());        // Hatvan Utca I/1
            orderObj.setBillingCity(order.getClientResult().getClientBillingAddress().getClientCity());            // Debrecen
            orderObj.setBillingZipCode(order.getClientResult().getClientBillingAddress().getClientZipCode());      // 4025
            orderObj.setBillingCountryId(order.getClientResult().getClientBillingAddress().getClientCountryId());  // hu
            orderObj.setBillingCountryName(order.getClientResult().getClientBillingAddress().getClientCountryName()); // Węgry
            orderObj.setBillingProvince(order.getClientResult().getClientBillingAddress().getClientProvince());    // ""
            orderObj.setBillingProvinceId(order.getClientResult().getClientBillingAddress().getClientProvinceId()); // ""
            orderObj.setBillingPhone1(order.getClientResult().getClientBillingAddress().getClientPhone1());        // +36 30 940 6113
            orderObj.setBillingPhone2(order.getClientResult().getClientBillingAddress().getClientPhone2());        // ""
            orderObj.setBillingNip(order.getClientResult().getClientBillingAddress().getClientNip());              // ""

            // 配送地址信息
            orderObj.setDeliveryAddressId(order.getClientResult().getClientDeliveryAddress().getClientDeliveryAddressId());           // 1144225
            orderObj.setDeliveryFirstName(order.getClientResult().getClientDeliveryAddress().getClientDeliveryAddressFirstName());    // Endrődi
            orderObj.setDeliveryLastName(order.getClientResult().getClientDeliveryAddress().getClientDeliveryAddressLastName());      // Réka
            orderObj.setDeliveryFirm(order.getClientResult().getClientDeliveryAddress().getClientDeliveryAddressFirm());             // ""
            orderObj.setDeliveryStreet(order.getClientResult().getClientDeliveryAddress().getClientDeliveryAddressStreet());         // Hatvan Utca I/1
            orderObj.setDeliveryCity(order.getClientResult().getClientDeliveryAddress().getClientDeliveryAddressCity());             // Debrecen
            orderObj.setDeliveryZipCode(order.getClientResult().getClientDeliveryAddress().getClientDeliveryAddressZipCode());       // 4025
            orderObj.setDeliveryCountryId(order.getClientResult().getClientDeliveryAddress().getClientDeliveryAddressCountryId());   // hu
            orderObj.setDeliveryCountryName(order.getClientResult().getClientDeliveryAddress().getClientDeliveryAddressCountry());   // Węgry
            orderObj.setDeliveryProvince(order.getClientResult().getClientDeliveryAddress().getClientDeliveryAddressProvince());     // ""
            orderObj.setDeliveryProvinceId(order.getClientResult().getClientDeliveryAddress().getClientDeliveryAddressProvinceId()); // ""
            orderObj.setDeliveryPhone1(order.getClientResult().getClientDeliveryAddress().getClientDeliveryAddressPhone1());         // +36 30 940 6113
            orderObj.setDeliveryPhone2(order.getClientResult().getClientDeliveryAddress().getClientDeliveryAddressPhone2());         // ""
            orderObj.setDeliveryAddressType(order.getClientResult().getClientDeliveryAddress().getClientDeliveryAddressType());      // client
            orderObj.setDeliveryPickupPointInternalId(order.getClientResult().getClientDeliveryAddress().getClientDeliveryAddressPickupPointInternalId()); // 0

            // 订单状态和备注信息
            orderObj.setOrderStatus(order.getOrderDetails().getOrderStatus());                    // on_order
            orderObj.setOrderAddDate(LocalDateTime.parse(order.getOrderDetails().getOrderAddDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));        // 2025-03-09 08:30:33
            orderObj.setOrderChangeDate(LocalDateTime.parse(order.getOrderDetails().getOrderChangeDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));   // 2025-03-09 08:30:33
            orderObj.setOrderDispatchDate(order.getOrderDetails().getOrderDispatchDate());       // 0
            orderObj.setOrderPrepareTime(order.getOrderDetails().getOrderPrepareTime());         // 0
            orderObj.setClientNoteToCourier(order.getOrderDetails().getClientNoteToCourier());   // ""
            orderObj.setClientNoteToOrder(order.getOrderDetails().getClientNoteToOrder());       // ""
            orderObj.setClientRequestInvoice(order.getOrderDetails().getClientRequestInvoice()); // n
            orderObj.setOrderNote(order.getOrderDetails().getOrderNote());                       // ""
            orderObj.setOrderOperatorLogin(order.getOrderDetails().getOrderOperatorLogin());     // ""
            orderObj.setApiFlag(order.getOrderDetails().getApiFlag());                           // none
            orderObj.setProductRemovedInStock(order.getOrderDetails().getProductRemovedInStock()); // y
            orderObj.setOrderBridgeNote(order.getOrderBridgeNote());                            // Zamówienie przeniesione przez IAI Bridge

            // 支付信息
            orderObj.setBillingCurrency(order.getOrderDetails().getPayments().getOrderBaseCurrency().getBillingCurrency());           // HUF
            orderObj.setCurrencyRate(BigDecimal.valueOf(order.getOrderDetails().getPayments().getOrderCurrency().getBillingCurrencyRate())); // 0.0105
            orderObj.setOrderDeliveryCost(BigDecimal.valueOf(order.getOrderDetails().getPayments().getOrderBaseCurrency().getOrderDeliveryCost())); // 2300
            orderObj.setOrderDeliveryVat(order.getOrderDetails().getPayments().getOrderBaseCurrency().getOrderDeliveryVat());         // 27
            orderObj.setOrderInsuranceCost(order.getOrderDetails().getPayments().getOrderBaseCurrency().getOrderInsuranceCost()); // 0
            orderObj.setOrderInsuranceVat(order.getOrderDetails().getPayments().getOrderBaseCurrency().getOrderInsuranceVat());       // 0
            orderObj.setOrderPayformCost(order.getOrderDetails().getPayments().getOrderBaseCurrency().getOrderPayformCost()); // 0
            orderObj.setOrderPayformVat(order.getOrderDetails().getPayments().getOrderBaseCurrency().getOrderPayformVat());           // 0
            orderObj.setOrderProductsCost(BigDecimal.valueOf(order.getOrderDetails().getPayments().getOrderBaseCurrency().getOrderProductsCost())); // 10180
            orderObj.setOrderPaymentDays(order.getOrderDetails().getPayments().getOrderPaymentDays());                                // 0
            orderObj.setOrderPaymentType(order.getOrderDetails().getPayments().getOrderPaymentType());                                // cash_on_delivery
            orderObj.setOrderRebatePercent(order.getOrderDetails().getPayments().getOrderRebatePercent());                           // 0
            orderObj.setOrderVatExists(order.getOrderDetails().getPayments().getOrderVatExists());                                    // y
            orderObj.setOrderWorthCalculateType(order.getOrderDetails().getPayments().getOrderWorthCalculateType());                  // gross

            // 配送信息
            orderObj.setCourierId(order.getOrderDetails().getDispatch().getCourierId());                     // 1040007
            orderObj.setCourierName(order.getOrderDetails().getDispatch().getCourierName());                 // Magyar Posta
            orderObj.setCourierWebserviceOnly(order.getOrderDetails().getDispatch().getCourierWebserviceOnly()); // false
            orderObj.setDeliveryDate(order.getOrderDetails().getDispatch().getDeliveryDate());              // ""
            orderObj.setDeliveryDateAdditional(order.getOrderDetails().getDispatch().getDeliveryDateAdditional()); // ""
            orderObj.setDeliveryPackageId(order.getOrderDetails().getDispatch().getDeliveryPackageId());    // PBJSM55860622
            orderObj.setDeliveryWeight(order.getOrderDetails().getDispatch().getDeliveryWeight());          // 2360
            orderObj.setEstimatedDeliveryDate(order.getOrderDetails().getDispatch().getEstimatedDeliveryDate());                                     // 2025-03-10 08:00:00

            // 订单来源信息
            orderObj.setSourceOrderId(order.getOrderDetails().getOrderSourceResults().getOrderSourceDetails().getOrderSourceId());     // 267
            orderObj.setSourceName(order.getOrderDetails().getOrderSourceResults().getOrderSourceDetails().getOrderSourceName());      // FacebookAds
            orderObj.setSourceType(order.getOrderDetails().getOrderSourceResults().getOrderSourceDetails().getOrderSourceType());      // price_comparer
            orderObj.setSourceTypeId(order.getOrderDetails().getOrderSourceResults().getOrderSourceDetails().getOrderSourceTypeId());  // 7
            orderObj.setSourcePageUrl(order.getOrderDetails().getOrderSourceResults().getOrderSourceDetails().getSourcePageUrl());     // http://m.facebook.com/
//            orderObj.setShopId(order.getOrderDetails().getOrderSourceResults().getShopId());                                          // 10
            orderObj.setEntryProductId(order.getOrderDetails().getOrderSourceResults().getOrderSourceDetails().getEntryProductIdBeforeOrder()); // 20201
            orderObj.setSourceFresh(order.getOrderDetails().getOrderSourceResults().getOrderSourceDetails().getFresh());              // n
            orderObj.setSourceFulfillment(order.getOrderDetails().getOrderSourceResults().getOrderSourceDetails().getFulfillment());  // n

            // 系统字段
            orderObj.setCreateTime(LocalDateTime.now());
            orderObj.setUpdateTime(LocalDateTime.now());

            // 订单商品列表
            List<OmsIdosellOrderItem> orderItems = new ArrayList<>();
            for (Order.OrderDetailsDTO.ProductsResultsDTO product : order.getOrderDetails().getProductsResults()) {
                OmsIdosellOrderItem item = new OmsIdosellOrderItem();
                
//                item.setOrderId(orderObj.getOrderId());                                    // endrodireka2-1
                item.setProductId(product.getProductId());                             // 14872
                item.setProductCode(product.getProductCode());                         // DX-47 GREEN
                item.setProductName(product.getProductName());                         // Zielone tenisówki Slip On Irving
                item.setVersionName(product.getVersionName());                         // Zielony
                item.setSizeId(product.getSizeId());                                   // B
                item.setSizePanelName(product.getSizePanelName());                    // 38
                item.setStockId(product.getStockId());                                // 13
                item.setProductQuantity(product.getProductQuantity());                 // 1
                item.setProductOrderPrice(BigDecimal.valueOf(product.getProductOrderPrice())); // 1519
                item.setProductOrderPriceNet(BigDecimal.valueOf(product.getProductOrderPriceNet())); // 1196
                item.setProductPanelPrice(BigDecimal.valueOf(product.getProductPanelPrice())); // 1519
                item.setProductPanelPriceNet(BigDecimal.valueOf(product.getProductPanelPriceNet())); // 1196
                item.setProductVat(product.getProductVat());                          // 27
                item.setProductWeight(product.getProductWeight());                    // 760
                item.setBasketPosition(product.getBasketPosition());                  // 0
                item.setBundleId(product.getBundleId());                             // 0
                item.setOrderSalesMode(product.getOrderSalesMode());                 // money
                item.setProductSizeCodeExternal(product.getProductSizeCodeExternal()); // 0000148721027
                item.setRemarksToProduct(product.getRemarksToProduct());             // ""
                
                item.setCreateTime(LocalDateTime.now());
                item.setUpdateTime(LocalDateTime.now());
                
                orderItems.add(item);
            }
            orderObj.setItems(orderItems);

            // 保存订单到数据库
            //插入订单数据
            var result = orderService.saveOrder(params.getShopId(), orderObj);
            if (result.getCode() == ResultVoEnum.DataExist.getIndex()) {
                //已经存在
                log.info("/**************主动更新idosell订单：开始更新数据库：" + orderObj.getOrderId() + "存在、更新************开始通知****/");
                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.DOU, MqType.ORDER_MESSAGE,orderObj.getOrderId()));
                hasExistOrder++;
            } else if (result.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
                log.info("/**************主动更新idosell订单：开始更新数据库：" + orderObj.getOrderId() + "不存在、新增************开始通知****/");
                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.DOU,MqType.ORDER_MESSAGE,orderObj.getOrderId()));
                insertSuccess++;
            } else {
                log.info("/**************主动更新idosell订单：开始更新数据库：" + orderObj.getOrderId() + "报错****************/");
                totalError++;
            }
        }
        if(totalError==0) {
            if (lasttime == null) {
                // 新增
                OShopPullLasttime insertLasttime = new OShopPullLasttime();
                insertLasttime.setShopId(params.getShopId());
                insertLasttime.setCreateTime(new Date());
                insertLasttime.setLasttime(endTime);
                insertLasttime.setPullType("ORDER");
                pullLasttimeService.save(insertLasttime);

            } else {
                // 修改
                OShopPullLasttime updateLasttime = new OShopPullLasttime();
                updateLasttime.setId(lasttime.getId());
                updateLasttime.setUpdateTime(new Date());
                updateLasttime.setLasttime(endTime);
                pullLasttimeService.updateById(updateLasttime);
            }
        }

        OShopPullLogs logs = new OShopPullLogs();
        logs.setShopType(EnumShopType.IDOSELL.getIndex());
        logs.setShopId(params.getShopId());
        logs.setPullType("ORDER");
        logs.setPullWay("主动拉取订单");
        logs.setPullParams(pullParams);
        logs.setPullResult("{insert:"+insertSuccess+",update:"+hasExistOrder+",fail:"+totalError+"}");
        logs.setPullTime(currDateTime);
        logs.setDuration(System.currentTimeMillis() - currTimeMillis);
        pullLogsService.save(logs);

        String msg = "成功{startTime:"+startTime.format(formatter)+",endTime:"+endTime.format(formatter)+"}总共找到：" + resultVo.getTotalRecords() + "条订单，新增：" + insertSuccess + "条，添加错误：" + totalError + "条，更新：" + hasExistOrder + "条";
        log.info("/**************主动更新idosell订单：END：" + msg + "****************/");
        return AjaxResult.success(msg);
    }
}
