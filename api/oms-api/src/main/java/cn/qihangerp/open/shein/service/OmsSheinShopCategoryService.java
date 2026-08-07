package cn.qihangerp.open.shein.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.module.open.shein.domain.OmsSheinShopCategory;
import cn.qihangerp.open.shein.request.CategoryRequest;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【oms_shein_shop_category】的数据库操作Service
* @createDate 2025-03-12 11:12:54
*/
public interface OmsSheinShopCategoryService extends IService<OmsSheinShopCategory> {
    PageResult<OmsSheinShopCategory> queryPageList(CategoryRequest bo, PageQuery pageQuery);
    void saveCategory(OmsSheinShopCategory category);
    void batchPushToOms();
}
