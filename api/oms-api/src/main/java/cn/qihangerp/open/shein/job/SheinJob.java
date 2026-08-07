//package cn.qihangerp.module.open.shein.job;
//
//import cn.qihangerp.common.config.RedisCache;
//import cn.qihangerp.common.enums.EnumShopType;
//import cn.qihangerp.common.utils.StringUtils;
//import cn.qihangerp.domain.OShopPlatform;
//import cn.qihangerp.module.goods.domain.OGoodsInventory;
//import cn.qihangerp.oms.service.OGoodsInventoryService;
//import cn.qihangerp.module.open.shein.domain.vo.SheinGoodsSku;
//import cn.qihangerp.module.open.shein.domain.vo.SheinOrder;
//import cn.qihangerp.module.open.shein.domain.vo.SheinOrderItem;
//import cn.qihangerp.module.open.shein.helper.SheinApiHelper;
//import cn.qihangerp.module.open.shein.helper.SheinOrderApiHelper;
//import cn.qihangerp.module.open.shein.helper.model.*;
//
//import cn.qihangerp.module.open.shein.service.ISheinOrderItemService;
//import cn.qihangerp.module.open.shein.service.ISheinOrderService;
//import cn.qihangerp.module.service.OShopPlatformService;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.util.CollectionUtils;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//@Configuration
//@EnableScheduling
//@Slf4j
//public class SheinJob {
//    @Autowired
//    private OShopPlatformService platformService;
//
//    @Autowired
//    private ISheinOrderService sheinOrderService;
//
//    @Autowired
//    private ISheinOrderItemService sheinOrderItemService;
//
//    @Autowired
//    private OGoodsInventoryService goodsInventoryService;
//
//    @Autowired
//    private RedisCache redisCache;
//
//    @Autowired
//    private ISheinGoodsSkuService sheinGoodsSkuService;
//
//    private static final String sheinInventorySkuCodeRedisKeyPrefix = "shein:inventory:skuCode:%s";
//
//    //@Scheduled(fixedRate = 50000)
//    private void orderList() {
//        OShopPlatform platform = platformService.selectById(EnumShopType.SHEIN.getIndex());
//        String appKey = platform.getAppKey();
//        String appSecret = platform.getAppSecret();
//        String serverUrl = platform.getServerUrl();
//        OrderListRequest request = new OrderListRequest();
//        request.setAppKey(appKey);
//        request.setAppSecret(appSecret);
//        request.setUrl(serverUrl);
//        request.setQueryType(1);
//        request.setStartTime("2023-12-12 15:38:29");
//        request.setEndTime("2024-12-12 15:38:29");
//        request.setPage(1);
//        request.setPageSize(30);
//        try {
//            OrderListResponse orderListResponse = SheinOrderApiHelper.queryOrderList(request);
//            System.out.printf("orderListResponse=%s\n", orderListResponse);
//
//            OrderListResponse.OrderListInfo info = orderListResponse.getInfo();
//            if (info == null) {
//                return;
//            }
//
//            List<OrderListResponse.OrderListDo> orderList = info.getOrderList();
//            if (CollectionUtils.isEmpty(orderList)) {
//                return;
//            }
//            for (OrderListResponse.OrderListDo orderListDo : orderList) {
//                String orderNo = orderListDo.getOrderNo();
//                SheinOrder sheinOrder = sheinOrderService.getOne(new LambdaQueryWrapper<>(SheinOrder.class).eq(SheinOrder::getOrderNo, orderNo));
//                if (sheinOrder != null) {
//                    continue;
//                }
//                //新增订单
//                sheinOrder = new SheinOrder();
//                sheinOrder.setOrderNo(orderNo);
//                sheinOrder.setOrderStatus(orderListDo.getOrderStatus());
//                sheinOrder.setOrderCreateTime(orderListDo.getOrderCreateTime());
//                sheinOrder.setOrderUpdateTime(orderListDo.getOrderUpdateTime());
//                sheinOrderService.save(sheinOrder);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    //@Scheduled(fixedRate = 50000)
//    private void orderDetail() {
//        OShopPlatform platform = platformService.selectById(EnumShopType.SHEIN.getIndex());
//        String appKey = platform.getAppKey();
//        String appSecret = platform.getAppSecret();
//        String serverUrl = platform.getServerUrl();
//
//        List<SheinOrder> unCompleteOrderList = sheinOrderService.list(new LambdaQueryWrapper<SheinOrder>().eq(SheinOrder::getCompleteStatus, 0));
//        if (CollectionUtils.isEmpty(unCompleteOrderList)) {
//            return;
//        }
//        OrderDetailRequest request = new OrderDetailRequest();
//        request.setAppKey(appKey);
//        request.setAppSecret(appSecret);
//        request.setUrl(serverUrl);
//        request.setOrderNoList(unCompleteOrderList.stream().map(SheinOrder::getOrderNo).toList());
//        try {
//            OrderDetailResponse orderDetailResponse = SheinOrderApiHelper.queryOrderDetail(request);
//
//            System.out.printf("orderDetailResponse=%s\n", orderDetailResponse);
//
//            List<OrderDetailResponse.OrderDetailInfo> info = orderDetailResponse.getInfo();
//            if (CollectionUtils.isEmpty(info)) {
//                return;
//            }
//            for (OrderDetailResponse.OrderDetailInfo orderDetailInfo : info) {
//                String orderNo = orderDetailInfo.getOrderNo();
//                SheinOrder sheinOrder = sheinOrderService.getOne(new LambdaQueryWrapper<>(SheinOrder.class).eq(SheinOrder::getOrderNo, orderNo));
//                sheinOrder.setOrderStatus(orderDetailInfo.getOrderStatus());
//
//                List<OrderDetailResponse.OrderGoodsDetailInfo> orderGoodsInfoList = orderDetailInfo.getOrderGoodsInfoList();
//                if (CollectionUtils.isEmpty(orderGoodsInfoList)) {
//                    continue;
//                }
//                for (OrderDetailResponse.OrderGoodsDetailInfo orderGoodsDetailInfo : orderGoodsInfoList) {
//                    String skuCode = orderGoodsDetailInfo.getSkuCode();
//                    Long goodsId = orderGoodsDetailInfo.getGoodsId();
//                    SheinOrderItem sourceItem = sheinOrderItemService.getOne(new LambdaQueryWrapper<>(SheinOrderItem.class).eq(SheinOrderItem::getGoodsId, goodsId));
//                    if (sourceItem != null) {
//                        continue;
//                    }
//                    sourceItem = new SheinOrderItem();
//                    sourceItem.setOrderNo(orderNo);
//                    sourceItem.setGoodsId(goodsId);
//                    sourceItem.setSkuCode(skuCode);
//                    sourceItem.setSpuName(orderGoodsDetailInfo.getSpuName());
//                    sourceItem.setGoodsStatus(orderGoodsDetailInfo.getNewGoodsStatus());
//                    sourceItem.setSellerCurrency(orderGoodsDetailInfo.getSaleCurrency());
//                    sourceItem.setSellerCurrencyPrice(orderGoodsDetailInfo.getSellerCurrencyPrice().toPlainString());
//                    sheinOrderItemService.save(sourceItem);
//                    //减库存
//                    SheinGoodsSku sheinGoodsSku = sheinGoodsSkuService.getOne(new LambdaQueryWrapper<>(SheinGoodsSku.class).eq(SheinGoodsSku::getSkuCode, skuCode));
//                    if (sheinGoodsSku == null){
//                        continue;
//                    }
//                    String oGoodsSkuId = sheinGoodsSku.getOGoodsSkuId();
//                    if (StringUtils.isEmpty(oGoodsSkuId)){
//                        continue;
//                    }
//                    OGoodsInventory one = goodsInventoryService.getOne(new LambdaQueryWrapper<>(OGoodsInventory.class).eq(OGoodsInventory::getSkuId, oGoodsSkuId));
//                    if (one == null){
//                        continue;
//                    }
//                    one.setQuantity(BigDecimal.valueOf(one.getQuantity()).subtract(BigDecimal.valueOf(1)).longValue());
//                }
//                sheinOrder.setCompleteStatus(1);
//                sheinOrderService.updateById(sheinOrder);
//            }
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    //@Scheduled(fixedRate = 50000)
//    private void syncGoodsInventory() {
//        OShopPlatform platform = platformService.selectById(EnumShopType.SHEIN.getIndex());
//        String appKey = platform.getAppKey();
//        String appSecret = platform.getAppSecret();
//        String serverUrl = platform.getServerUrl();
//        List<OGoodsInventory> oGoodsInventories = goodsInventoryService.list(new LambdaQueryWrapper<>(OGoodsInventory.class).gt(
//                OGoodsInventory::getQuantity, BigDecimal.ONE
//        ));
//
//        Map<String, SheinGoodsSku> goodsSkuIdMap = sheinGoodsSkuService.list(new LambdaQueryWrapper<>(SheinGoodsSku.class).in(SheinGoodsSku::getOGoodsSkuId,
//                        oGoodsInventories.stream().map(OGoodsInventory::getSkuId).toList()))
//                .stream().collect(Collectors.toMap(SheinGoodsSku::getOGoodsSkuId, Function.identity(), (e1, e2) -> e1));
//
//        List<OGoodsInventory> needUpdateGoodsInventoryList = new ArrayList<>();
//        for (OGoodsInventory oGoodsInventory : oGoodsInventories) {
//            String skuCode = oGoodsInventory.getSkuCode();
//            String redisKey = String.format(sheinInventorySkuCodeRedisKeyPrefix, skuCode);
//            Long quantity = redisCache.getCacheObject(redisKey);
//            if (oGoodsInventory.getQuantity().equals(quantity)) {
//                continue;
//            }
//            redisCache.setCacheObject(redisKey, oGoodsInventory.getQuantity());
//
//            SheinGoodsSku sheinGoodsSku = goodsSkuIdMap.get(oGoodsInventory.getSkuId());
//            if (sheinGoodsSku == null) {
//                continue;
//            }
//            needUpdateGoodsInventoryList.add(oGoodsInventory);
//        }
//
//        UpdateGoodsInventoryRequest request = new UpdateGoodsInventoryRequest();
//        request.setAppKey(appKey);
//        request.setAppSecret(appSecret);
//        request.setUrl(serverUrl);
//        List<UpdateGoodsInventoryRequest.UpdateGoodsInventoryHolder> updateSkuInventoryQuantityRequests = needUpdateGoodsInventoryList.stream().map(needUpdateGoodsInventory ->
//        {
//            UpdateGoodsInventoryRequest.UpdateGoodsInventoryHolder holder = new UpdateGoodsInventoryRequest.UpdateGoodsInventoryHolder();
//
//            holder.setSkuCode(needUpdateGoodsInventory.getSkuCode());
//            holder.setSkc(needUpdateGoodsInventory.getSkuCode());
//            holder.setAvailableNumber(needUpdateGoodsInventory.getQuantity().toString());
//            return holder;
//        }).toList();
//        request.setStock(updateSkuInventoryQuantityRequests);
//        try {
//            UpdateGoodsInventoryResponse updateGoodsInventoryResponse = SheinApiHelper.syncGoodsInventory(request);
//            log.info("updateGoodsInventoryResponse={}", updateGoodsInventoryResponse);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
