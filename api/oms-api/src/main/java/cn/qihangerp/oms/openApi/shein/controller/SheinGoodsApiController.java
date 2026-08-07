package cn.qihangerp.oms.openApi.shein.controller;

import cn.qihangerp.oms.openApi.PullRequest;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.domain.OShopPlatform;
import cn.qihangerp.open.shein.service.SheinGoodsCommonService;
import cn.qihangerp.oms.service.OShopPlatformService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/open-api/shein/goods")
@AllArgsConstructor
public class SheinGoodsApiController extends BaseController {

    private final SheinGoodsCommonService sheinGoodsCommonService;

    private final OShopPlatformService platformService;

    /**
     * API拉取全量数据
     */
    @PostMapping("/pull")
    public AjaxResult pullGoods(@RequestBody PullRequest request) {
        if(request.getShopId() == null||request.getShopId()<=0) return AjaxResult.error("缺少参数：shopId");
        try {
            OShopPlatform platform = platformService.selectById(EnumShopType.SHEIN.getIndex());
            sheinGoodsCommonService.pullProductAndSkuList(request.getShopId(),platform.getAppKey(),platform.getAppSecret(),platform.getServerUrl());
//            sheinGoodsCommonService.pullProductList(platform.getAppKey(),platform.getAppSecret(),platform.getServerUrl());
//            sheinGoodsCommonService.pullProductSkuList(platform.getAppKey(),platform.getAppSecret(),platform.getServerUrl());
            return AjaxResult.success();
        } catch (Exception e) {
            log.error("拉取商品数据异常", e);
            return error(e.getMessage());
        }
    }
} 