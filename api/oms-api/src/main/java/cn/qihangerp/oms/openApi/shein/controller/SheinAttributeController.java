package cn.qihangerp.oms.openApi.shein.controller;

import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.module.open.shein.domain.OmsSheinProductAttr;
import cn.qihangerp.module.open.shein.domain.OmsSheinProductAttrVal;
import cn.qihangerp.open.shein.service.OmsSheinProductAttrService;
import cn.qihangerp.open.shein.service.OmsSheinProductAttrValService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/open-api/shein/attribute")
public class SheinAttributeController extends BaseController {
    private final OmsSheinProductAttrService productAttrService;
    private final OmsSheinProductAttrValService attributeValueService;
    @GetMapping("/list")
    public TableDataInfo list(OmsSheinProductAttr bo, PageQuery pageQuery)
    {
        var pageList = productAttrService.queryPageList(bo,pageQuery);
        return getDataTable(pageList);
    }

    /**
     * 获取商品品牌详细信息
     */
    @GetMapping(value = "value_list/{id}")
    public TableDataInfo getInfo(@PathVariable("id") Long id)
    {
        List<OmsSheinProductAttrVal> value = attributeValueService.getValueByAttributeId(id);
        return getDataTable(value);
    }

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
