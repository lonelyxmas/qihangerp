package cn.qihangerp.open.idosell.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.utils.IdUtils;
import cn.qihangerp.module.goods.domain.OGoodsInventory;
import cn.qihangerp.module.goods.domain.OGoodsInventoryRecord;
import cn.qihangerp.module.goods.mapper.OGoodsInventoryMapper;
import cn.qihangerp.module.goods.mapper.OGoodsInventoryRecordMapper;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellOrderItem;
import cn.qihangerp.module.open.idosell.domain.bo.OrderSearchParam;
import cn.qihangerp.module.open.idosell.domain.vo.IdosellGoods;
import cn.qihangerp.module.open.idosell.domain.vo.IdosellGoodsSku;
import cn.qihangerp.module.open.idosell.mapper.IdosellGoodsMapper;
import cn.qihangerp.module.open.idosell.mapper.IdosellGoodsSkuMapper;
import cn.qihangerp.module.open.idosell.mapper.OmsIdosellOrderItemMapper;
import cn.qihangerp.module.order.domain.OOrder;
import cn.qihangerp.module.order.domain.OOrderItem;
import cn.qihangerp.module.order.mapper.OOrderItemMapper;
import cn.qihangerp.module.order.mapper.OOrderMapper;
import cn.qihangerp.module.wms.domain.OGoodsStockOut;
import cn.qihangerp.module.wms.domain.OGoodsStockOutItem;
import cn.qihangerp.module.wms.mapper.OGoodsStockOutItemMapper;
import cn.qihangerp.module.wms.mapper.OGoodsStockOutMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellOrder;
import cn.qihangerp.open.idosell.service.OmsIdosellOrderService;
import cn.qihangerp.module.open.idosell.mapper.OmsIdosellOrderMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author qilip
* @description 针对表【oms_idosell_order】的数据库操作Service实现
* @createDate 2025-03-09 18:36:37
*/
@Slf4j
@AllArgsConstructor
@Service
public class OmsIdosellOrderServiceImpl extends ServiceImpl<OmsIdosellOrderMapper, OmsIdosellOrder>
    implements OmsIdosellOrderService{
    private final OOrderMapper orderMapper;
    private final OOrderItemMapper orderItemMapper;
    private OmsIdosellOrderMapper idosellOrderMapper;
    private OmsIdosellOrderItemMapper idosellOrderItemMapper;
    private IdosellGoodsSkuMapper idosellGoodsSkuMapper;
    private IdosellGoodsMapper idosellGoodsMapper;
    private final OGoodsStockOutMapper goodsStockOutMapper;
    private final OGoodsStockOutItemMapper goodsStockOutItemMapper;
    private final OGoodsInventoryMapper goodsInventoryMapper;
    private final OGoodsInventoryRecordMapper goodsInventoryRecordMapper;

    private final String DATE_PATTERN =
            "^(?:(?:(?:\\d{4}-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|1\\d|2[0-8]))|(?:(?:(?:\\d{2}(?:0[48]|[2468][048]|[13579][26])|(?:(?:0[48]|[2468][048]|[13579][26])00))-0?2-29))$)|(?:(?:(?:\\d{4}-(?:0?[13578]|1[02]))-(?:0?[1-9]|[12]\\d|30))$)|(?:(?:(?:\\d{4}-0?[13-9]|1[0-2])-(?:0?[1-9]|[1-2]\\d|30))$)|(?:(?:(?:\\d{2}(?:0[48]|[13579][26]|[2468][048])|(?:(?:0[48]|[13579][26]|[2468][048])00))-0?2-29))$)$";
    private final Pattern DATE_FORMAT = Pattern.compile(DATE_PATTERN);
    @Override
    public PageResult<OmsIdosellOrder> queryPageList(OrderSearchParam param, PageQuery pageQuery) {
        if(org.springframework.util.StringUtils.hasText(param.getStartTime())){
            Matcher matcher = DATE_FORMAT.matcher(param.getStartTime());
            boolean b = matcher.find();
            if(b){
                param.setStartTime(param.getStartTime()+" 00:00:00");
            }
        }
        if(org.springframework.util.StringUtils.hasText(param.getEndTime())){
            Matcher matcher = DATE_FORMAT.matcher(param.getEndTime());
            boolean b = matcher.find();
            if(b){
                param.setEndTime(param.getEndTime()+" 23:59:59");
            }
        }

        LambdaQueryWrapper<OmsIdosellOrder> queryWrapper = new LambdaQueryWrapper<OmsIdosellOrder>()
                .eq(param.getShopId()!=null,OmsIdosellOrder::getShopId,param.getShopId())
                .eq(StringUtils.hasText(param.getOrderId()),OmsIdosellOrder::getOrderId,param.getOrderId())
                .eq(StringUtils.hasText(param.getOrderStatus()),OmsIdosellOrder::getOrderStatus,param.getOrderStatus())
                .ge(StringUtils.hasText(param.getStartTime()),OmsIdosellOrder::getOrderAddDate, param.getStartTime())
                .le(StringUtils.hasText(param.getEndTime()),OmsIdosellOrder::getOrderAddDate,param.getEndTime())
                ;
        pageQuery.setOrderByColumn("order_add_date");
        pageQuery.setIsAsc("desc");
        Page<OmsIdosellOrder> taoGoodsPage = idosellOrderMapper.selectPage(pageQuery.build(), queryWrapper);
        if(taoGoodsPage.getRecords()!=null){
            for (var order:taoGoodsPage.getRecords()) {
                order.setItems(idosellOrderItemMapper.selectList(new LambdaQueryWrapper<OmsIdosellOrderItem>().eq(OmsIdosellOrderItem::getOrderId,order.getId())));
            }
        }
        return PageResult.build(taoGoodsPage);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo<Long> saveOrder(Long shopId, OmsIdosellOrder order) {
        if(order == null ) return ResultVo.error(ResultVoEnum.SystemException);
        try {
            List<OmsIdosellOrder> orders = idosellOrderMapper.selectList(new LambdaQueryWrapper<OmsIdosellOrder>().eq(OmsIdosellOrder::getOrderId, order.getOrderId()));
            if(orders.isEmpty()){
                // 新增
                order.setShopId(shopId);
                idosellOrderMapper.insert(order);
                for(var item:order.getItems()){
                    item.setOrderId(order.getId());
                    idosellOrderItemMapper.insert(item);
                }
                saveOOrderAndStockOut(order,shopId);
            }else{
                order.setId(orders.get(0).getId());
                order.setUpdateTime(LocalDateTime.now());
                order.setShopId(shopId);
                idosellOrderMapper.updateById(order);
                for(var item:order.getItems()){
                    List<OmsIdosellOrderItem> items = idosellOrderItemMapper.selectList(new LambdaQueryWrapper<OmsIdosellOrderItem>()
                            .eq(OmsIdosellOrderItem::getOrderId, order.getId())
                            .eq(OmsIdosellOrderItem::getProductId, item.getProductId())
                            .eq(OmsIdosellOrderItem::getSizeId, item.getSizeId())
                    );
                    if(items.isEmpty()){
                        //新增
                        item.setOrderId(order.getId());
                        idosellOrderItemMapper.insert(item);
                    }else{
                        item.setId(items.get(0).getId());
                        item.setUpdateTime(LocalDateTime.now());
                        idosellOrderItemMapper.updateById(item);
                    }
                }
                saveOOrderAndStockOut(order,shopId);
            }

            return ResultVo.success();
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            log.info("保存订单数据错误："+e.getMessage());
            return ResultVo.error(ResultVoEnum.SystemException, "系统异常：" + e.getMessage());
        }
    }

    protected void saveOOrderAndStockOut(OmsIdosellOrder order, Long shopId) {
        List<OOrder> oOrders = orderMapper.selectList(
                new LambdaQueryWrapper<OOrder>()
                        .eq(OOrder::getShopId, shopId)
                        .eq(OOrder::getOrderNum, order.getOrderSerialNumber()));
        Long orderId = null;
        //售后状态 1：无售后或售后关闭，2：售后处理中，3：退款中，4： 退款成功
        //订单状态0：新订单，1：待发货，2：已发货，3：已完成，11已取消；12退款中；21待付款；22锁定，29删除，101部分发货;

        //订单状态；packed
        //new
        //payment_waiting
        //canceled
        //joined

        int refundStatus = 1;
        int orderStatus = 0;
        if (order.getOrderStatus().equals("payment_waiting")) {
            orderStatus = 21;
        } else if (order.getOrderStatus().equals("new")) {
            orderStatus = 1;
        } else if (order.getOrderStatus().equals("packed")) {
            orderStatus = 2;
        } else if (order.getOrderStatus().equals("canceled")) {
            orderStatus = 11;
            refundStatus = 4;
        } else if (order.getOrderStatus().equals("joined")) {
            orderStatus = 2;
        }
        if (oOrders.isEmpty()) {
            // 新增订单库订单（OOrder
            OOrder oOrder = new OOrder();
            oOrder.setOrderNum(order.getOrderSerialNumber().toString());
            oOrder.setShopType(EnumShopType.IDOSELL.getIndex());
            oOrder.setShopId(shopId);
            oOrder.setTag(order.getSourceName());
            oOrder.setRefundStatus(refundStatus);
            oOrder.setOrderStatus(orderStatus);
            oOrder.setOrderTime(Date.from(order.getOrderAddDate().atZone(ZoneId.systemDefault()).toInstant()));
            oOrder.setAmount(0.0);
            oOrder.setDeliveryCountryId(order.getDeliveryCountryId());
            oOrder.setDeliveryCountryName(order.getDeliveryCountryName());
            oOrder.setBillingCurrency(order.getBillingCurrency());
            orderMapper.insert(oOrder);
            orderId = oOrder.getId();
        } else {
            orderId = oOrders.get(0).getId();
            // 修改订单
            OOrder update = new OOrder();
            update.setId(orderId);
            update.setRefundStatus(refundStatus);
            update.setOrderStatus(orderStatus);
            update.setUpdateTime(new Date());
            update.setUpdateBy("同步更新");
            orderMapper.updateById(update);
        }
        // 处理子订单
        List<OOrderItem> newItemList = new ArrayList<>();

        for (var item : order.getItems()) {
            String originSkuId = item.getProductId()+item.getSizeId();
            List<OOrderItem> oOrderItems = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OOrderItem>()
                            .eq(OOrderItem::getOrderId, orderId)
                            .eq(OOrderItem::getSkuId, originSkuId));
            Long oGoodsId = 0L;
            Long oGoodsSkuId = 0L;
            String skuName = "";
            String goodsImg="";
            String goodsNum = "";
            // 查找idosell skuid
            List<IdosellGoodsSku> idosellGoodsSkus = idosellGoodsSkuMapper.selectList(new LambdaQueryWrapper<IdosellGoodsSku>()
                    .eq(IdosellGoodsSku::getProductId, item.getProductId())
                    .eq(IdosellGoodsSku::getSizeId, item.getSizeId())
            );
            if(!idosellGoodsSkus.isEmpty()) {
                if(idosellGoodsSkus.get(0).getOGoodsId()!=null){
                    oGoodsId = idosellGoodsSkus.get(0).getOGoodsId();
                }
                if(idosellGoodsSkus.get(0).getOGoodsSkuId()!=null){
                    oGoodsSkuId = idosellGoodsSkus.get(0).getOGoodsSkuId();
                }

                skuName = idosellGoodsSkus.get(0).getColorName()+" "+ idosellGoodsSkus.get(0).getSizeName();
            }

            List<IdosellGoods> idosellGoodsList = idosellGoodsMapper.selectList(new LambdaQueryWrapper<IdosellGoods>().eq(IdosellGoods::getProductId, item.getProductId()));
            if(!idosellGoodsList.isEmpty()) {
                goodsImg = idosellGoodsList.get(0).getMainImage();
                goodsNum = idosellGoodsList.get(0).getGoodsNum();
            }



            if (oOrderItems.isEmpty()) {
                // 添加订单库item
                OOrderItem oOrderItem = new OOrderItem();
                oOrderItem.setOrderId(orderId);
                oOrderItem.setOrderNum(order.getOrderSerialNumber().toString());
                oOrderItem.setSkuId(originSkuId);
                oOrderItem.setGoodsId(oGoodsId);
                oOrderItem.setGoodsSkuId(oGoodsSkuId);
                oOrderItem.setGoodsTitle(item.getProductName());
                oOrderItem.setGoodsImg(goodsImg);
                oOrderItem.setGoodsNum(goodsNum);
                oOrderItem.setSkuNum("");
                oOrderItem.setGoodsSpec(skuName);
                oOrderItem.setGoodsPrice(item.getProductOrderPrice());
                oOrderItem.setItemAmount(item.getProductOrderPriceNet());
                oOrderItem.setPayment(item.getProductOrderPriceNet());
                oOrderItem.setDiscountAmount(item.getProductOrderPrice().subtract(item.getProductOrderPriceNet()));
                oOrderItem.setQuantity(item.getProductQuantity());
                oOrderItem.setRefundStatus(refundStatus);
                oOrderItem.setOrderStatus(orderStatus);
//                    oOrderItem.setInventoryStatus(1);
                oOrderItem.setCreateTime(new Date());
                oOrderItem.setCreateBy("拉取订单");
                orderItemMapper.insert(oOrderItem);
                newItemList.add(oOrderItem);
            } else {
                // 修改
                OOrderItem update = new OOrderItem();
                update.setId(oOrderItems.get(0).getId());
                update.setSkuId(originSkuId);
                update.setGoodsId(oGoodsId);
                update.setGoodsSkuId(oGoodsSkuId);
                update.setGoodsImg(goodsImg);
                update.setGoodsNum(goodsNum);
                update.setGoodsSpec(skuName);
                update.setRefundStatus(refundStatus);
                update.setOrderStatus(orderStatus);
                orderItemMapper.updateById(update);
                newItemList.add(oOrderItems.get(0));
            }


        }
        // 减少库存
        //出库清单
        List<OGoodsStockOutItem> goodsStockOutItemList = new ArrayList<>();
        Set<Long> goodsIds = new HashSet<>();
        int total = 0;

        for (var item : newItemList) {
            if (item.getGoodsSkuId() > 0&&item.getInventoryStatus()==0) {
                // 减少库存
                goodsIds.add(item.getGoodsId());
                total += item.getQuantity();
                OGoodsStockOutItem oGoodsStockOutItem = new OGoodsStockOutItem();
                oGoodsStockOutItem.setStockOutType(1);
                oGoodsStockOutItem.setSourceOrderId(orderId);
                oGoodsStockOutItem.setSourceOrderItemId(item.getId());
                oGoodsStockOutItem.setSourceOrderNum(item.getOrderNum());
                oGoodsStockOutItem.setGoodsId(item.getGoodsId());
                oGoodsStockOutItem.setSkuId(item.getGoodsSkuId());
                oGoodsStockOutItem.setGoodsName(item.getGoodsTitle());
                oGoodsStockOutItem.setGoodsImg(item.getGoodsImg());
                oGoodsStockOutItem.setGoodsNum(item.getGoodsNum());
                oGoodsStockOutItem.setSkuNum(item.getSkuNum());
                oGoodsStockOutItem.setQuantity(item.getQuantity());
                oGoodsStockOutItem.setOutQuantity(item.getQuantity());
                oGoodsStockOutItem.setCompleteTime(new Date());
                oGoodsStockOutItem.setStatus(2);
                oGoodsStockOutItem.setCreateTime(new Date());
                goodsStockOutItemList.add(oGoodsStockOutItem);
            }
        }
        // 出库
        log.info("====== 添加orderItem成功=====开始减少库存");
        if(goodsStockOutItemList.size()>0) {
            OGoodsStockOut oGoodsStockOut = new OGoodsStockOut();
            oGoodsStockOut.setStockOutNum(IdUtils.simpleUUID());
            oGoodsStockOut.setStockOutType(1);
            oGoodsStockOut.setSourceId(orderId);
            oGoodsStockOut.setSourceNum(order.getOrderSerialNumber().toString());
            oGoodsStockOut.setGoodsGroup(goodsIds.size());
            oGoodsStockOut.setSkuGroup(goodsStockOutItemList.size());
            oGoodsStockOut.setTotalQuantity(total);
            oGoodsStockOut.setOutQuantity(total);
            oGoodsStockOut.setCompleteTime(new Date());
            oGoodsStockOut.setCreateTime(new Date());
            oGoodsStockOut.setStatus(2);
            oGoodsStockOut.setCreateBy("同步订单减库存");
            oGoodsStockOut.setRemark("同步订单减库存");
            goodsStockOutMapper.insert(oGoodsStockOut);
            for (var item : goodsStockOutItemList) {
                item.setStockOutId(oGoodsStockOut.getId());
                goodsStockOutItemMapper.insert(item);
                // 更新子订单状态
                OOrderItem update = new OOrderItem();
                update.setId(item.getSourceOrderItemId());
                update.setInventoryStatus(1);
                orderItemMapper.updateById(update);
                // 减库存
                Integer balanceQty=0;
                OGoodsInventory goodsInventory = null;
                List<OGoodsInventory> inventories = goodsInventoryMapper.selectList(new LambdaQueryWrapper<OGoodsInventory>().eq(OGoodsInventory::getSkuId, item.getSkuId()));
                if(inventories.size()>0) {
                    goodsInventory = inventories.get(0);
                    // 减库存
                    balanceQty = goodsInventory.getQuantity()-item.getQuantity();
                    OGoodsInventory goodsInventoryUpdate = new OGoodsInventory();
                    goodsInventoryUpdate.setId(goodsInventory.getId());
                    goodsInventoryUpdate.setQuantity(balanceQty);
                    goodsInventoryUpdate.setUpdateBy("订单扣减库存");
                    goodsInventoryUpdate.setUpdateTime(new Date());
                    goodsInventoryMapper.updateById(goodsInventoryUpdate);
                }else{
                    balanceQty = 0- item.getQuantity();
                    // 新增一条记录
                    goodsInventory = new OGoodsInventory();
                    goodsInventory.setSkuId(item.getSkuId());
                    goodsInventory.setGoodsId(item.getGoodsId());
                    goodsInventory.setGoodsNum(item.getGoodsNum());
                    goodsInventory.setSkuCode(item.getSkuNum());
                    goodsInventory.setQuantity(balanceQty);
                    goodsInventory.setCreateTime(new Date());
                    goodsInventory.setCreateBy("订单扣减库存新增记录");
                    goodsInventoryMapper.insert(goodsInventory);
                }

                // 新增一条记录
                OGoodsInventoryRecord goodsInventoryRecord = new OGoodsInventoryRecord();
                goodsInventoryRecord.setInventoryId(goodsInventory.getId());
                goodsInventoryRecord.setGoodsId(item.getGoodsId());
                goodsInventoryRecord.setGoodsNum(item.getGoodsNum());
                goodsInventoryRecord.setSkuId(item.getSkuId());
                goodsInventoryRecord.setSkuCode(item.getSkuNum());
                goodsInventoryRecord.setType(2);//库存类型（1增加库存2减少库存3锁定库存）
                goodsInventoryRecord.setQuantity(item.getQuantity());
                goodsInventoryRecord.setLockedQuantity(0);
                goodsInventoryRecord.setBalanceQuantity(balanceQty);
                goodsInventoryRecord.setBizType(40);//业务类型（0初始化库存10采购入库20采购退货30退货入库40订单出库）
                goodsInventoryRecord.setBizId(oGoodsStockOut.getId());
                goodsInventoryRecord.setBizNum(oGoodsStockOut.getStockOutNum());
                goodsInventoryRecord.setBizItemId(item.getId());
                goodsInventoryRecord.setStatus(1);
                goodsInventoryRecord.setCreateTime(new Date());
                goodsInventoryRecord.setCreateBy("订单出库");
                goodsInventoryRecordMapper.insert(goodsInventoryRecord);
            }
        }
    }
}




