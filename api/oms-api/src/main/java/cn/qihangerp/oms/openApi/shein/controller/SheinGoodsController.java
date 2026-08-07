package cn.qihangerp.oms.openApi.shein.controller;

import cn.qihangerp.security.common.BaseController;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.module.open.shein.domain.OmsSheinGoods;

import cn.qihangerp.open.shein.request.SheinGoodsBo;
import cn.qihangerp.open.shein.service.OmsSheinGoodsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/open-api/shein/goods")
@RestController
@AllArgsConstructor
public class SheinGoodsController extends BaseController {

    @Autowired
    private OmsSheinGoodsService sheinGoodsService;


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo goodsList(SheinGoodsBo bo, PageQuery pageQuery) {
        PageResult<OmsSheinGoods> result = sheinGoodsService.queryPageList(bo, pageQuery);
        return getDataTable(result);
    }
    @RequestMapping(value = "/sync", method = RequestMethod.POST)
    public AjaxResult goodsSync(@RequestBody SheinGoodsBo bo) {
        //idosellGoodsService.batchSync();spuName
        var result = sheinGoodsService.sync(bo);
        if(result.getCode()==0)
            return AjaxResult.success();
        else return AjaxResult.error(result.getMsg());
    }

    @RequestMapping(value = "/batch-sync", method = RequestMethod.POST)
    public AjaxResult batchSync() {
        sheinGoodsService.batchSync();
        return AjaxResult.success();
    }
}
