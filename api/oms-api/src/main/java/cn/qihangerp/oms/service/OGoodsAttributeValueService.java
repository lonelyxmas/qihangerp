package cn.qihangerp.oms.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.module.goods.domain.OGoodsAttributeValue;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【o_goods_attribute_value(商品属性值表)】的数据库操作Service
* @createDate 2025-03-13 11:47:05
*/
public interface OGoodsAttributeValueService extends IService<OGoodsAttributeValue> {
    PageResult<OGoodsAttributeValue> queryPageList(OGoodsAttributeValue bo, PageQuery pageQuery);
}
