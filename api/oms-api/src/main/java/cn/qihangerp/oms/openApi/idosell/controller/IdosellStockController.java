package cn.qihangerp.oms.openApi.idosell.controller;

import cn.qihangerp.oms.openApi.idosell.request.IdosellStockToOmsRequest;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.common.*;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.enums.HttpStatus;
import cn.qihangerp.domain.OShop;
import cn.qihangerp.domain.OShopPlatform;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellGoodsSkuStock;
import cn.qihangerp.module.open.idosell.domain.bo.IdosellGoodsStockBo;
import cn.qihangerp.open.idosell.service.IdosellStockCommonService;
import cn.qihangerp.open.idosell.service.OmsIdosellGoodsSkuStockService;
import cn.qihangerp.oms.service.OShopPlatformService;
import cn.qihangerp.oms.service.OShopService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/open-api/idosell/stock")
@AllArgsConstructor
public class IdosellStockController extends BaseController {
//    private final IIdosellGoodsSkuService idosellGoodsSkuService;
    private final OShopService shopService;
    private final OShopPlatformService platformService;
//    private final IdosellStockApiHelper stockApiHelper;
    private final IdosellStockCommonService stockCommonService;
    private final OmsIdosellGoodsSkuStockService goodsSkuStockService;

    /**
     * 库存列表
     * @param bo
     * @param pageQuery
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo goodsList(IdosellGoodsStockBo bo, PageQuery pageQuery) {
        PageResult<OmsIdosellGoodsSkuStock> result = goodsSkuStockService.queryPageList(bo, pageQuery);
        return getDataTable(result);
    }
    /**
     * API拉取全量数据
     */
    @GetMapping("/pull")
    public AjaxResult pullGoods(@RequestParam(required = true) Long shopId) throws IOException {
        if (shopId == null || shopId <= 0) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:missing parameter shopId");
        }
        OShop shop = shopService.getById(shopId);
        if(shop == null)return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:shop not exist");
        else if(shop.getType().intValue() != EnumShopType.IDOSELL.getIndex()) return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:shop type not exist");

        OShopPlatform platform = platformService.selectById(EnumShopType.IDOSELL.getIndex());
        if(platform == null)return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:platform not exist");
        else if (!StringUtils.hasText(platform.getAppKey())) return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:appKey not exist");
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
        ResultVo resultVo = goodsSkuStockService.pushStockToOms(bo.getId());
        if(resultVo.getCode()==0)
            return AjaxResult.success();
        else return AjaxResult.error(resultVo.getMsg());
    }

} 