package cn.qihangerp.open.idosell.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.module.open.idosell.domain.bo.CategorySearchParam;
import cn.qihangerp.module.open.idosell.domain.bo.IdosellGoodsBo;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellShopCategory;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【oms_idosell_shop_category】的数据库操作Service
* @createDate 2025-03-12 10:39:50
*/
public interface OmsIdosellShopCategoryService extends IService<OmsIdosellShopCategory> {
    PageResult<OmsIdosellShopCategory> queryPageList(CategorySearchParam bo, PageQuery pageQuery);
    void saveCategory(OmsIdosellShopCategory category);
    ResultVo bindOmsCategory(Integer id,Long categoryId);
}
