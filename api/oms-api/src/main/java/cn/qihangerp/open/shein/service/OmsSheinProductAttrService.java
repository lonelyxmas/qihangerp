package cn.qihangerp.open.shein.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.module.goods.domain.OGoodsAttribute;
import cn.qihangerp.module.open.shein.domain.OmsSheinProductAttr;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【oms_shein_product_attr(商品属性)】的数据库操作Service
* @createDate 2025-03-13 21:49:03
*/
public interface OmsSheinProductAttrService extends IService<OmsSheinProductAttr> {
    PageResult<OmsSheinProductAttr> queryPageList(OmsSheinProductAttr bo, PageQuery pageQuery);
}
