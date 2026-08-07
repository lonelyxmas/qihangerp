package cn.qihangerp.oms.openApi.idosell.controller;

import cn.qihangerp.oms.openApi.BindOmsAttributeRequest;
import cn.qihangerp.oms.openApi.BindOmsAttributeValueRequest;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellSaleAttr;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellSaleAttrVal;
import cn.qihangerp.open.idosell.service.OmsIdosellSaleAttrService;
import cn.qihangerp.open.idosell.service.OmsIdosellSaleAttrValService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/open-api/idosell/attribute")
public class IdosellAttributeController extends BaseController {
private final OmsIdosellSaleAttrService saleAttrService;
private final OmsIdosellSaleAttrValService saleAttrValService;

    @GetMapping("/list")
    public TableDataInfo list(OmsIdosellSaleAttr bo, PageQuery pageQuery)
    {
        var pageList = saleAttrService.queryPageList(bo,pageQuery);
        return getDataTable(pageList);
    }

//    @GetMapping("/value_list")
//    public TableDataInfo valueList(OGoodsAttributeValue bo, PageQuery pageQuery)
//    {
//        var pageList = attributeValueService.queryPageList(bo,pageQuery);
//        return getDataTable(pageList);
//    }

    /**
     * 获取商品品牌详细信息
     */
    @GetMapping(value = "/value_list/{id}")
    public TableDataInfo getValueList(@PathVariable("id") Long id)
    {
        List<OmsIdosellSaleAttrVal> valueList = saleAttrValService.getValueByAttributeId(id);
        return getDataTable(valueList);
    }

    /**
     * 绑定
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/bind_oms_attribute", method = RequestMethod.POST)
    public AjaxResult bindOmsAttribute(@RequestBody BindOmsAttributeRequest request) throws Exception {
        if (request.getAttributeId() == null || request.getAttributeId() <= 0) return AjaxResult.error("缺少参数：id");
        if (request.getOmsAttributeId() == null || request.getOmsAttributeId() <= 0)
            return AjaxResult.error("缺少参数：OmsAttributeId");
        ResultVo resultVo = saleAttrService.bindOmsAttribute(request.getAttributeId(), request.getOmsAttributeId());
        if (resultVo.getCode() == 0) return AjaxResult.success();
        else return AjaxResult.error(resultVo.getMsg());
    }
    @RequestMapping(value = "/bind_oms_attribute_value", method = RequestMethod.POST)
    public AjaxResult bindOmsAttributeValue(@RequestBody BindOmsAttributeValueRequest request) throws Exception {
        if (request.getAttributeValueId() == null || request.getAttributeValueId() <= 0) return AjaxResult.error("缺少参数：id");
        if (request.getOmsAttributeValueId() == null || request.getOmsAttributeValueId() <= 0)
            return AjaxResult.error("缺少参数：OmsAttributeValueId");
        ResultVo resultVo = saleAttrValService.bindOmsAttributeValue(request.getAttributeValueId(), request.getOmsAttributeValueId());
        if (resultVo.getCode() == 0) return AjaxResult.success();
        else return AjaxResult.error(resultVo.getMsg());
    }
//
//    /**
//     * 新增商品品牌
//     */
//    @PostMapping
//    public AjaxResult add(@RequestBody OGoodsBrand erpGoodsBrand)
//    {
//        erpGoodsBrand.setStatus(1);
//        erpGoodsBrand.setCreateBy(getUsername());
//        erpGoodsBrand.setCreateTime(new Date());
//        return toAjax(brandService.save(erpGoodsBrand));
//    }
//
//    /**
//     * 修改商品品牌
//     */
//    @PutMapping
//    public AjaxResult edit(@RequestBody OGoodsBrand erpGoodsBrand)
//    {
//        erpGoodsBrand.setUpdateBy(getUsername());
//        erpGoodsBrand.setUpdateTime(new Date());
//        return toAjax(brandService.updateById(erpGoodsBrand));
//    }
//
//    /**
//     * 删除商品品牌
//     */
//    @DeleteMapping("/{ids}")
//    public AjaxResult remove(@PathVariable Long[] ids)
//    {
//        return toAjax(brandService.removeByIds(Arrays.stream(ids).toList()));
//    }
}
