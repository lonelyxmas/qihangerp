package cn.qihangerp.oms.controller;

import cn.qihangerp.common.*;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.domain.OShop;
import cn.qihangerp.module.goods.domain.OGoodsInventory;
import cn.qihangerp.module.goods.domain.OGoodsInventoryRecord;
import cn.qihangerp.module.goods.domain.bo.OGoodsInventoryOperateBo;
import cn.qihangerp.oms.service.OGoodsInventoryRecordService;
import cn.qihangerp.oms.service.OGoodsInventoryService;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.open.idosell.service.IdosellStockCommonService;
import cn.qihangerp.open.shein.service.SheinStockCommonService;
import cn.qihangerp.oms.service.OShopService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/oms-api/goodsInventory")
public class GoodsInventoryController extends BaseController {
    private final OGoodsInventoryService goodsInventoryService;
    private final OGoodsInventoryRecordService inventoryRecordService;
    private final OShopService shopService;
    private final IdosellStockCommonService idosellStockCommonService;
    private final SheinStockCommonService sheinStockCommonService;
    @GetMapping("/list")
    public TableDataInfo list(OGoodsInventory bo, PageQuery pageQuery)
    {
        PageResult<OGoodsInventory> pageResult = goodsInventoryService.queryPageList(bo, pageQuery);
        return getDataTable(pageResult);
    }

    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
//        OGoodsInventory goodsInventory = goodsInventoryService.getById(id);
//        if(goodsInventory!=null) {
            List<OGoodsInventoryRecord> list = inventoryRecordService.list(
                    new LambdaQueryWrapper<OGoodsInventoryRecord>()
                            .eq(OGoodsInventoryRecord::getInventoryId, id));
            return AjaxResult.success(list);
//        }
//        return success();
    }
    @PostMapping(value = "/update")
    public AjaxResult update(@RequestBody OGoodsInventoryOperateBo bo){
        ResultVo resultVo = goodsInventoryService.updateInventory(bo);
        if(resultVo.getCode()==0)
            return AjaxResult.success();
        else  return AjaxResult.error(resultVo.getMsg());
    }


    @PostMapping(value = "/syncToShop")
    public AjaxResult syncToShop(@RequestBody OGoodsInventoryOperateBo request) {
        if(request.getId()==null) return AjaxResult.error("Param Error:Id");
        if(request.getShopId()==null) return AjaxResult.error("Param Error:ShopId");
        OShop shop = shopService.getById(request.getShopId());
        if(shop==null) return AjaxResult.error("店铺不存在");
        if(shop.getType().intValue() == EnumShopType.SHEIN.getIndex()) {
            var result = sheinStockCommonService.pushShopStock(request.getShopId(), request.getId());
            if(result.getCode()==ResultVoEnum.SUCCESS.getIndex()) return AjaxResult.success();
            else return AjaxResult.error(result.getMsg());
        }else if(shop.getType().intValue() == EnumShopType.IDOSELL.getIndex()) {
            var result = idosellStockCommonService.pushShopStock(request.getShopId(), request.getId());
            if(result.getCode()==ResultVoEnum.SUCCESS.getIndex()) return AjaxResult.success();
            else return AjaxResult.error(result.getMsg());
        }else{
            return AjaxResult.error("暂不支持");
        }
    }

}
