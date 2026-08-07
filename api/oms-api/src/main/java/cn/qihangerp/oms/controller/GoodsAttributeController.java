package cn.qihangerp.oms.controller;

import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.module.goods.domain.OGoodsAttribute;
import cn.qihangerp.module.goods.domain.OGoodsAttributeValue;
import cn.qihangerp.oms.service.OGoodsAttributeService;
import cn.qihangerp.oms.service.OGoodsAttributeValueService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/oms-api/goods_attribute")
public class GoodsAttributeController extends BaseController {
    private final OGoodsAttributeService attributeService;
    private final OGoodsAttributeValueService attributeValueService;
    @GetMapping("/list")
    public TableDataInfo list(OGoodsAttribute bo, PageQuery pageQuery)
    {
        var pageList = attributeService.queryPageList(bo,pageQuery);
        return getDataTable(pageList);
    }

    @GetMapping("/value_list")
    public TableDataInfo valueList(OGoodsAttributeValue bo, PageQuery pageQuery)
    {
        var pageList = attributeValueService.queryPageList(bo,pageQuery);
        return getDataTable(pageList);
    }

    /**
     * 获取商品品牌详细信息
     */
//    @GetMapping(value = "/{id}")
//    public AjaxResult getInfo(@PathVariable("id") Long id)
//    {
//        return success(brandService.getById(id));
//    }
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
