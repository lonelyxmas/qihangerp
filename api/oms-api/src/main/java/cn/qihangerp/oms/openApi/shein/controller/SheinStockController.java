package cn.qihangerp.oms.openApi.shein.controller;

import cn.qihangerp.oms.openApi.idosell.request.IdosellStockToOmsRequest;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.common.*;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.enums.HttpStatus;
import cn.qihangerp.domain.OShop;
import cn.qihangerp.domain.OShopPlatform;
import cn.qihangerp.module.open.shein.domain.OmsSheinGoodsSkuStock;

import cn.qihangerp.module.open.shein.domain.bo.SheinGoodsStockBo;
import cn.qihangerp.open.shein.service.OmsSheinGoodsSkuStockService;
import cn.qihangerp.open.shein.service.SheinStockCommonService;
import cn.qihangerp.oms.service.OShopPlatformService;
import cn.qihangerp.oms.service.OShopService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/open-api/shein/stock")
@AllArgsConstructor
public class SheinStockController extends BaseController {

    private final OShopService shopService;
    private final OShopPlatformService platformService;
    private final OmsSheinGoodsSkuStockService skuStockService;
    private final SheinStockCommonService stockCommonService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo goodsList(SheinGoodsStockBo bo, PageQuery pageQuery) {
        PageResult<OmsSheinGoodsSkuStock> result = skuStockService.queryPageList(bo, pageQuery);
        return getDataTable(result);
    }

    /**
     * API拉取全量数据
     */
    @GetMapping("/pull")
    public AjaxResult pullGoods(@RequestParam(required = true) Long shopId) {
        if (shopId == null || shopId <= 0) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:missing parameter shopId");
        }

        OShop shop = shopService.getById(shopId);
        if(shop == null)return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:shop not exist");
        else if(shop.getType().intValue() != EnumShopType.SHEIN.getIndex()) return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:shop type not exist");

//        else if(!StringUtils.hasText(shop.getApiRequestUrl())) return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:missing api request url");
//        else if(!StringUtils.hasText(shop.getAppKey())) return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:missing api key");
        OShopPlatform platform = platformService.selectById(EnumShopType.SHEIN.getIndex());
        if(platform == null)return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:platform not exist");
        else if (!StringUtils.hasText(platform.getAppKey())) return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:appKey not exist");
        else if (!StringUtils.hasText(platform.getAppSecret())) return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:appSecret not exist");
        else if(!StringUtils.hasText(platform.getServerUrl())) return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:serverUrl not exist");


        try {
            stockCommonService.pullShopStockList(shopId,platform.getAppKey(),platform.getAppSecret(),platform.getServerUrl());
            return AjaxResult.success();
        } catch (Exception e) {
            log.error("拉取商品数据异常", e);
            return error(e.getMessage());
        }
    }

    @RequestMapping(value = "/pushToOms", method = RequestMethod.POST)
    public AjaxResult pushToOms(@RequestBody IdosellStockToOmsRequest bo) {
        if(bo.getId()==null) return AjaxResult.error("Param Error：id");
        ResultVo resultVo = skuStockService.pushStockToOms(bo.getId());
        if(resultVo.getCode()==0)
            return AjaxResult.success();
        else return AjaxResult.error(resultVo.getMsg());
    }
} 