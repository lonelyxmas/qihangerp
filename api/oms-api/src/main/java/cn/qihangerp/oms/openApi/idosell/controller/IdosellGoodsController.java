package cn.qihangerp.oms.openApi.idosell.controller;

import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.module.open.idosell.domain.bo.IdosellGoodsBo;
import cn.qihangerp.module.open.idosell.domain.vo.IdosellGoods;
import cn.qihangerp.open.idosell.service.IIdosellGoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/open-api/idosell/goods")
@RestController
@RequiredArgsConstructor
public class IdosellGoodsController extends BaseController {

    private final IIdosellGoodsService idosellGoodsService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo goodsList(IdosellGoodsBo bo, PageQuery pageQuery) {
        PageResult<IdosellGoods> result = idosellGoodsService.queryPageList(bo, pageQuery);
        return getDataTable(result);
    }

    @RequestMapping(value = "/sync", method = RequestMethod.POST)
    public AjaxResult goodsSync(@RequestBody IdosellGoodsBo bo) {
        //idosellGoodsService.batchSync();
        var result = idosellGoodsService.sync(bo);
        if(result.getCode()==0)
            return AjaxResult.success();
        else return AjaxResult.error(result.getMsg());

    }

    @RequestMapping(value = "/batch-sync", method = RequestMethod.POST)
    public AjaxResult batchSync() {
        idosellGoodsService.batchSync();
        return AjaxResult.success();
    }
}
