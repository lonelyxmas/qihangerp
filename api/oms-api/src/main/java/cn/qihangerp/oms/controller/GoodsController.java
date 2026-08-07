package cn.qihangerp.oms.controller;


import cn.qihangerp.oms.request.GoodsSyncRequest;
import cn.qihangerp.common.*;

import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.domain.OShop;
import cn.qihangerp.module.goods.domain.OGoods;
import cn.qihangerp.module.goods.domain.OGoodsSku;
import cn.qihangerp.module.goods.domain.vo.GoodsSpecListVo;
import cn.qihangerp.oms.service.OGoodsPublishService;
import cn.qihangerp.oms.service.OGoodsService;
import cn.qihangerp.oms.service.OGoodsSkuService;
import cn.qihangerp.open.idosell.service.IdosellGoodsCommonService;
import cn.qihangerp.open.shein.service.SheinGoodsCommonService;

import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.oms.service.OShopService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 商品管理Controller
 * 
 * @author qihang
 * @date 2023-12-29
 */
@AllArgsConstructor
@RestController
@RequestMapping("/api/oms-api/goods")
public class GoodsController extends BaseController
{
    private final OGoodsService goodsService;
    private final OGoodsSkuService skuService;
    private final SheinGoodsCommonService sheinGoodsCommonService;
    private final IdosellGoodsCommonService idosellGoodsCommonService;
    private final OGoodsPublishService goodsPublishService;
    private final OShopService shopService;
    /**
     * 搜索商品SKU
     * 条件：商品编码、SKU、商品名称
     */
    @GetMapping("/searchSku")
    public TableDataInfo searchSkuBy(String keyword)
    {
        List<GoodsSpecListVo> list = goodsService.searchGoodsSpec(keyword);
        return getDataTable(list);
    }

    @GetMapping("/sku_list")
    public TableDataInfo skuList(OGoodsSku bo, PageQuery pageQuery)
    {
        var pageList = goodsService.querySkuPageList(bo,pageQuery);
        return getDataTable(pageList);
    }

    /**
     * 查询商品管理列表
     */
    @PreAuthorize("@ss.hasPermi('goods:goods:list')")
    @GetMapping("/list")
    public TableDataInfo list(OGoods goods,PageQuery pageQuery)
    {
        PageResult<OGoods> pageList = goodsService.queryPageList(goods, pageQuery);
        List<OGoods> records = pageList.getRecords();
        if(!records.isEmpty()){
            for(OGoods record : records){
                record.setPublishList(goodsPublishService.getPulishListByGoodsId(record.getId()));
            }
        }
//        Map<Long,Integer> goodsAndCheckStatusMap = sheinGoodsCommonService.selectSheinCheckStatus(records.stream().map(OGoods::getId).toList());
//        records = records.stream().peek(item-> {
//            Integer sheinStatus = goodsAndCheckStatusMap.get(Long.valueOf(item.getId()));
//            item.setSheinCheckStatus(goodsAndCheckStatusMap.get(Long.valueOf(item.getId())));
//            if (sheinStatus != null) {
//                item.setSyncStatus(1);
//            } else {
//                item.setSyncStatus(0);
//            }
//        }).toList();
        pageList.setRecords(records);
        return getDataTable(pageList);
    }

    /**
     * 获取商品管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('goods:goods:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(goodsService.selectGoodsById(id));
    }
    /**
     * 获取商品管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('goods:goods:query')")
    @GetMapping(value = "/sku/{id}")
    public AjaxResult getSkuInfo(@PathVariable("id") Long id)
    {
        return success(skuService.getById(id));
    }
    /**
     * 新增商品管理
     */
//    @PreAuthorize("@ss.hasPermi('goods:goods:add')")
//    @PostMapping("/add")
//    public AjaxResult add(@RequestBody GoodsAddBo goods)
//    {
//        ResultVo<Long> resultVo = goodsService.insertGoods(getUsername(), goods);
//        if(resultVo.getCode()!=0) return AjaxResult.error(resultVo.getMsg());
//        else return AjaxResult.success(resultVo.getData());
////        goods.setCreateBy(getUsername());
////        int result = goodsService.insertGoods(goods);
////        if(result == -1) new AjaxResult(501,"商品编码已存在");
////        return toAjax(1);
//    }

//    @PreAuthorize("@ss.hasPermi('goods:goods:add')")
//    @PostMapping("/goodsSku")
//    public AjaxResult addSku(@RequestBody OGoodsSku goodsSku)
//    {
//
//        int result = goodsService.insertGoodsSku(goodsSku);
//        if(result == -1) new AjaxResult(501,"商品编码已存在");
//        return toAjax(1);
//    }

    /**
     * 修改商品管理
     */
    @PreAuthorize("@ss.hasPermi('goods:goods:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody OGoods goods)
    {
        return toAjax(goodsService.updateGoods(goods));
    }

    /**
     * 修改商品基本资料
     * @param sku
     * @return
     */
    @PutMapping("/sku")
    public AjaxResult editSku(@RequestBody OGoodsSku sku)
    {
        return toAjax(skuService.updateById(sku));
    }


    /**
     * 商品同步
     */
    @PostMapping("/sync")
    public AjaxResult publishToShein(@RequestBody GoodsSyncRequest request) throws IOException {
        if(request.getGoodsId()==null) return AjaxResult.error("请选择商品");
        if(request.getShopId()==null) return AjaxResult.error("请选择店铺");
        OShop shop = shopService.getById(request.getShopId());
        if(shop==null) return AjaxResult.error("店铺不存在");
        if(shop.getType().intValue() == EnumShopType.SHEIN.getIndex()) {
            var result = sheinGoodsCommonService.publishToShein(request.getGoodsId(), request.getShopId());
            if(result.getCode()==ResultVoEnum.SUCCESS.getIndex()) return AjaxResult.success();
            else return AjaxResult.error(result.getMsg());
        }else if(shop.getType().intValue() == EnumShopType.IDOSELL.getIndex()) {
            var result = idosellGoodsCommonService.publishToIdosell(request.getGoodsId(), request.getShopId());
            if(result.getCode()==ResultVoEnum.SUCCESS.getIndex()) return AjaxResult.success();
            else return AjaxResult.error(result.getMsg());
        }else{
            return AjaxResult.error("暂不支持");
        }
    }
}
