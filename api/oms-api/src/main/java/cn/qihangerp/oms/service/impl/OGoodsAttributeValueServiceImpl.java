package cn.qihangerp.oms.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.goods.domain.OGoodsAttributeValue;
import cn.qihangerp.oms.service.OGoodsAttributeValueService;
import cn.qihangerp.module.goods.mapper.OGoodsAttributeValueMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
* @author qilip
* @description 针对表【o_goods_attribute_value(商品属性值表)】的数据库操作Service实现
* @createDate 2025-03-13 11:47:05
*/
@AllArgsConstructor
@Service
public class OGoodsAttributeValueServiceImpl extends ServiceImpl<OGoodsAttributeValueMapper, OGoodsAttributeValue>
    implements OGoodsAttributeValueService{
    private final OGoodsAttributeValueMapper oGoodsAttributeValueMapper;

    @Override
    public PageResult<OGoodsAttributeValue> queryPageList(OGoodsAttributeValue bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OGoodsAttributeValue> queryWrapper = new LambdaQueryWrapper<OGoodsAttributeValue>();
        queryWrapper.eq(OGoodsAttributeValue::getAttributeId, bo.getAttributeId());
        queryWrapper.eq(StringUtils.hasText(bo.getAttributeValue()), OGoodsAttributeValue::getAttributeValue, bo.getAttributeValue());
        queryWrapper.eq(bo.getAttributeValueId()!=null, OGoodsAttributeValue::getAttributeValueId, bo.getAttributeValueId());

        pageQuery.setOrderByColumn("attribute_value_id");
        pageQuery.setIsAsc("asc");
        Page<OGoodsAttributeValue> pages = oGoodsAttributeValueMapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(pages);
    }
}




