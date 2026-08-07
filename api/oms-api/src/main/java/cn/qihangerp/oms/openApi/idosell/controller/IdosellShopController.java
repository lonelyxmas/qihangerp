package cn.qihangerp.oms.openApi.idosell.controller;

import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.module.open.idosell.domain.bo.CategorySearchParam;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellShopCategory;
import cn.qihangerp.open.idosell.service.OmsIdosellShopCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/open-api/idosell/shop")
@RestController
@RequiredArgsConstructor
public class IdosellShopController extends BaseController {

    private final OmsIdosellShopCategoryService shopCategoryService;



    /**
     * 拉取商品列表（包含sku）
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/category_list", method = RequestMethod.GET)
    public TableDataInfo categoryList(CategorySearchParam bo, PageQuery pageQuery) throws Exception {

        PageResult<OmsIdosellShopCategory> result = shopCategoryService.queryPageList(bo, pageQuery);
        return getDataTable(result);
    }
}
