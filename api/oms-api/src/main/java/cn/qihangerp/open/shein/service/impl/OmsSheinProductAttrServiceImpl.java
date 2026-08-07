package cn.qihangerp.open.shein.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.open.shein.domain.OmsSheinProductAttr;
import cn.qihangerp.open.shein.service.OmsSheinProductAttrService;
import cn.qihangerp.module.open.shein.mapper.OmsSheinProductAttrMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
* @author qilip
* @description 针对表【oms_shein_product_attr(商品属性)】的数据库操作Service实现
* @createDate 2025-03-13 21:49:03
*/
@AllArgsConstructor
@Service
public class OmsSheinProductAttrServiceImpl extends ServiceImpl<OmsSheinProductAttrMapper, OmsSheinProductAttr>
    implements OmsSheinProductAttrService{
    private final OmsSheinProductAttrMapper osSheinProductAttrMapper;

    @Override
    public PageResult<OmsSheinProductAttr> queryPageList(OmsSheinProductAttr bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OmsSheinProductAttr> queryWrapper = new LambdaQueryWrapper<OmsSheinProductAttr>();

        queryWrapper.eq(StringUtils.hasText(bo.getAttributeName()), OmsSheinProductAttr::getAttributeName, bo.getAttributeName());
        queryWrapper.eq(bo.getAttributeId()!=null, OmsSheinProductAttr::getAttributeId, bo.getAttributeId());
        queryWrapper.eq(bo.getAttributeType()!=null, OmsSheinProductAttr::getAttributeType, bo.getAttributeType());
        queryWrapper.eq(bo.getAttributeLabel()!=null, OmsSheinProductAttr::getAttributeLabel, bo.getAttributeLabel());
        queryWrapper.eq(bo.getAttributeStatus()!=null, OmsSheinProductAttr::getAttributeStatus, bo.getAttributeStatus());

        pageQuery.setOrderByColumn("attribute_type");
        pageQuery.setIsAsc("asc");
        Page<OmsSheinProductAttr> pages = osSheinProductAttrMapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(pages);
    }
}




