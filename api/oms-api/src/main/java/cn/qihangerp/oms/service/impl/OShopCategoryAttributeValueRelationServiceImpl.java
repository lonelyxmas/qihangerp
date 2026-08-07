package cn.qihangerp.oms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.goods.domain.OGoodsAttributeValueRelation;
import cn.qihangerp.oms.service.OShopCategoryAttributeValueRelationService;
import cn.qihangerp.module.goods.mapper.OGoodsAttributeValueRelationMapper;
import org.springframework.stereotype.Service;

/**
* @author qilip
* @description 针对表【o_shop_category_attribute_value_relation(店铺分类属性值（color和size）关联关系)】的数据库操作Service实现
* @createDate 2025-03-13 00:04:23
*/
@Service
public class OShopCategoryAttributeValueRelationServiceImpl extends ServiceImpl<OGoodsAttributeValueRelationMapper, OGoodsAttributeValueRelation>
    implements OShopCategoryAttributeValueRelationService{

}




