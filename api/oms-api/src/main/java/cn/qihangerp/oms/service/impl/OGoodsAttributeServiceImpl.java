package cn.qihangerp.oms.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.goods.domain.OGoodsAttribute;
import cn.qihangerp.oms.service.OGoodsAttributeService;
import cn.qihangerp.module.goods.mapper.OGoodsAttributeMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
* @author qilip
* @description 针对表【o_goods_attribute(商品属性表)】的数据库操作Service实现
* @createDate 2025-03-13 11:47:05
*/
@AllArgsConstructor
@Service
public class OGoodsAttributeServiceImpl extends ServiceImpl<OGoodsAttributeMapper, OGoodsAttribute>
    implements OGoodsAttributeService{
    private final OGoodsAttributeMapper mapper;
    @Override
    public PageResult<OGoodsAttribute> queryPageList(OGoodsAttribute bo, PageQuery pageQuery) {

        LambdaQueryWrapper<OGoodsAttribute> queryWrapper = new LambdaQueryWrapper<OGoodsAttribute>();

        queryWrapper.eq(StringUtils.hasText(bo.getAttributeName()), OGoodsAttribute::getAttributeName, bo.getAttributeName());
        queryWrapper.eq(bo.getAttributeId()!=null, OGoodsAttribute::getAttributeId, bo.getAttributeId());
        queryWrapper.eq(bo.getAttributeType()!=null, OGoodsAttribute::getAttributeType, bo.getAttributeType());
        queryWrapper.eq(bo.getAttributeLabel()!=null, OGoodsAttribute::getAttributeLabel, bo.getAttributeLabel());
        queryWrapper.eq(bo.getAttributeStatus()!=null, OGoodsAttribute::getAttributeStatus, bo.getAttributeStatus());

        pageQuery.setOrderByColumn("attribute_type");
        pageQuery.setIsAsc("asc");
        Page<OGoodsAttribute> pages = mapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(pages);
    }
}




