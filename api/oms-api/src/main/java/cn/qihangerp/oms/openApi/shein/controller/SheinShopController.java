package cn.qihangerp.oms.openApi.shein.controller;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.open.shein.request.CategoryRequest;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.module.open.shein.domain.OmsSheinShopCategory;
import cn.qihangerp.open.shein.service.OmsSheinProductTypeAttrService;
import cn.qihangerp.open.shein.service.OmsSheinShopCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/open-api/shein/shop")
@RestController
@RequiredArgsConstructor
public class SheinShopController extends BaseController {
    private final OmsSheinShopCategoryService sheinShopCategoryService;
    private final OmsSheinProductTypeAttrService attrService;

    /**
     * 拉取商品列表（包含sku）
     *
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/category_list", method = RequestMethod.GET)
    public TableDataInfo categoryList(CategoryRequest bo, PageQuery pageQuery) throws Exception {

        PageResult<OmsSheinShopCategory> result = sheinShopCategoryService.queryPageList(bo, pageQuery);
        return getDataTable(result);
    }

    @GetMapping(value = "/productTypeAttr/{productTypeId}")
    public AjaxResult getAttr(@PathVariable("productTypeId") Long productTypeId) {
        return success(attrService.getAttrByProductTypeId(productTypeId));
    }


}
