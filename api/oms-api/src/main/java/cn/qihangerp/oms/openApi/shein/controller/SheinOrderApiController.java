package cn.qihangerp.oms.openApi.shein.controller;

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
import cn.qihangerp.domain.OShopPlatform;
import cn.qihangerp.domain.OShopPullLasttime;
import cn.qihangerp.domain.OShopPullLogs;
import cn.qihangerp.open.idosell.helper.IdosellOrderApiHelper;
import cn.qihangerp.module.open.shein.domain.OmsSheinPurchaseOrder;
import cn.qihangerp.module.open.shein.domain.OmsSheinPurchaseOrderItem;
import cn.qihangerp.open.shein.helper.SheinOrderApiHelper;
import cn.qihangerp.open.shein.helper.request.PurchaseOrderListRequest;
import cn.qihangerp.open.shein.helper.response.SheinPurchaseOrderResponse;
import cn.qihangerp.open.shein.service.OmsSheinPurchaseOrderService;
import cn.qihangerp.oms.service.OShopPlatformService;
import cn.qihangerp.oms.service.OShopPullLasttimeService;
import cn.qihangerp.oms.service.OShopPullLogsService;
import cn.qihangerp.oms.service.OShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RequestMapping("/api/open-api/shein/order")
@RestController
@RequiredArgsConstructor
public class SheinOrderApiController extends BaseController {
    private final OShopService shopService;
    private final IdosellOrderApiHelper idosellOrderApiHelper;
    private final MqUtils mqUtils;
    private final OShopPullLogsService pullLogsService;
    private final OShopPullLasttimeService pullLasttimeService;
    private final OmsSheinPurchaseOrderService orderService;
    private final OShopPlatformService platformService;
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
        else if(shop.getType().intValue() != EnumShopType.SHEIN.getIndex()) return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:shop type not exist");

//        else if(!StringUtils.hasText(shop.getApiRequestUrl())) return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:missing api request url");
//        else if(!StringUtils.hasText(shop.getAppKey())) return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:missing api key");
        OShopPlatform platform = platformService.selectById(EnumShopType.SHEIN.getIndex());
        if(platform == null)return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:platform not exist");
        else if (!StringUtils.hasText(platform.getAppKey())) return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:appKey not exist");
        else if (!StringUtils.hasText(platform.getAppSecret())) return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:appSecret not exist");
        else if(!StringUtils.hasText(platform.getServerUrl())) return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:serverUrl not exist");

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
        log.info("===========主动拉取Shein订单，时间段====="+pullParams);
        PurchaseOrderListRequest request = new PurchaseOrderListRequest();
        request.setPageNumber(0);
        request.setPageSize(20);
        request.setUpdateTimeStart("2025-02-23 10:29:59");
        request.setUpdateTimeEnd("2025-03-09 10:29:59");
//        request.setAppKey(platform.getAppKey());
//        request.setAppSecret(platform.getAppSecret());
//        request.setUrl(platform.getServerUrl());
        // 调用查询接口
        SheinPurchaseOrderResponse response = SheinOrderApiHelper.queryPurchaseOrderList(platform.getServerUrl(),platform.getAppKey(),platform.getAppSecret(),request);

        if(response.getCode() !=0 ){
            OShopPullLogs logs = new OShopPullLogs();
            logs.setShopId(params.getShopId());
            logs.setShopType(EnumShopType.IDOSELL.getIndex());
            logs.setPullType("ORDER");
            logs.setPullWay("主动拉取订单");
            logs.setPullParams(pullParams);
            logs.setPullResult(response.getMsg());
            logs.setPullTime(currDateTime);
            logs.setDuration(System.currentTimeMillis() - currTimeMillis);
            pullLogsService.save(logs);
            return AjaxResult.error(15003,"接口拉取错误："+response.getMsg());
        }
        int insertSuccess = 0;//新增成功的订单
        int totalError = 0;
        int hasExistOrder = 0;//已存在的订单数

        if(response.getInfo()!=null&&response.getInfo().getCount()>0) {
            //循环插入订单数据到数据库
            for (var order : response.getInfo().getOrderList()) {
                // 创建订单对象并赋值
                OmsSheinPurchaseOrder purchaseOrder = new OmsSheinPurchaseOrder();
                BeanUtils.copyProperties(order, purchaseOrder);
                List<OmsSheinPurchaseOrderItem> purchaseOrderItemList = new ArrayList<>();
                if (!order.getOrderExtends().isEmpty()) {
                    for (var item : order.getOrderExtends()) {
                        OmsSheinPurchaseOrderItem purchaseOrderItem = new OmsSheinPurchaseOrderItem();
                        BeanUtils.copyProperties(item, purchaseOrderItem);
                        purchaseOrderItemList.add(purchaseOrderItem);
                    }
                }
                purchaseOrder.setItems(purchaseOrderItemList);

                //插入订单数据
                var result = orderService.saveOrder(params.getShopId(), purchaseOrder);
                if (result.getCode() == ResultVoEnum.DataExist.getIndex()) {
                    //已经存在
                    log.info("/**************主动更新shein订单：开始更新数据库：" + purchaseOrder.getOrderNo() + "存在、更新************开始通知****/");
                    mqUtils.sendApiMessage(MqMessage.build(EnumShopType.DOU, MqType.ORDER_MESSAGE, purchaseOrder.getOrderNo()));
                    hasExistOrder++;
                } else if (result.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
                    log.info("/**************主动更新idosell订单：开始更新数据库：" + purchaseOrder.getOrderNo() + "不存在、新增************开始通知****/");
                    mqUtils.sendApiMessage(MqMessage.build(EnumShopType.DOU, MqType.ORDER_MESSAGE, purchaseOrder.getOrderNo()));
                    insertSuccess++;
                } else {
                    log.info("/**************主动更新idosell订单：开始更新数据库：" + purchaseOrder.getOrderNo() + "报错****************/");
                    totalError++;
                }
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
        logs.setShopType(EnumShopType.SHEIN.getIndex());
        logs.setShopId(params.getShopId());
        logs.setPullType("ORDER");
        logs.setPullWay("主动拉取订单");
        logs.setPullParams(pullParams);
        logs.setPullResult("{insert:"+insertSuccess+",update:"+hasExistOrder+",fail:"+totalError+"}");
        logs.setPullTime(currDateTime);
        logs.setDuration(System.currentTimeMillis() - currTimeMillis);
        pullLogsService.save(logs);

        String msg = "成功{startTime:"+startTime.format(formatter)+",endTime:"+endTime.format(formatter)+"}总共找到：" + response.getInfo().getCount() + "条订单，新增：" + insertSuccess + "条，添加错误：" + totalError + "条，更新：" + hasExistOrder + "条";
        log.info("/**************主动更新Shein订单：END：" + msg + "****************/");
        return AjaxResult.success(msg);
    }
}
