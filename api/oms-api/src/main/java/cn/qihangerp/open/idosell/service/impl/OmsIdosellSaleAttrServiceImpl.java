package cn.qihangerp.open.idosell.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.module.goods.domain.OGoodsAttributeRelation;
import cn.qihangerp.oms.service.OGoodsAttributeRelationService;
import cn.qihangerp.oms.service.OGoodsAttributeService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellSaleAttr;
import cn.qihangerp.open.idosell.service.OmsIdosellSaleAttrService;
import cn.qihangerp.module.open.idosell.mapper.OmsIdosellSaleAttrMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author qilip
* @description 针对表【oms_idosell_sale_attr(idosell销售属性)】的数据库操作Service实现
* @createDate 2025-03-13 20:52:43
*/
@Slf4j
@AllArgsConstructor
@Service
public class OmsIdosellSaleAttrServiceImpl extends ServiceImpl<OmsIdosellSaleAttrMapper, OmsIdosellSaleAttr>
    implements OmsIdosellSaleAttrService{
    private final OmsIdosellSaleAttrMapper osIdosellSaleAttrMapper;
    private final OGoodsAttributeService goodsAttributeService;
    private final OGoodsAttributeRelationService goodsAttributeRelationService;

    @Override
    public PageResult<OmsIdosellSaleAttr> queryPageList(OmsIdosellSaleAttr bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OmsIdosellSaleAttr> queryWrapper = new LambdaQueryWrapper<OmsIdosellSaleAttr>();

//        queryWrapper.eq(StringUtils.hasText(bo.getAttributeName()), OGoodsAttribute::getAttributeName, bo.getAttributeName());
//        queryWrapper.eq(bo.getAttributeId()!=null, OGoodsAttribute::getAttributeId, bo.getAttributeId());
//        queryWrapper.eq(bo.getAttributeType()!=null, OGoodsAttribute::getAttributeType, bo.getAttributeType());
//        queryWrapper.eq(bo.getAttributeLabel()!=null, OGoodsAttribute::getAttributeLabel, bo.getAttributeLabel());
//        queryWrapper.eq(bo.getAttributeStatus()!=null, OGoodsAttribute::getAttributeStatus, bo.getAttributeStatus());

        pageQuery.setOrderByColumn("attribute_type");
        pageQuery.setIsAsc("asc");
        Page<OmsIdosellSaleAttr> pages = osIdosellSaleAttrMapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(pages);
    }
    @Transactional
    @Override
    public ResultVo bindOmsAttribute(Long attributeId, Long omsAttributeId) {


        OmsIdosellSaleAttr attr = osIdosellSaleAttrMapper.selectById(attributeId);
        if (attr == null) {
            return ResultVo.error(1500, "数据不存在");
        }
        var omsAttr = goodsAttributeService.getById(omsAttributeId);
        if (omsAttr == null) return ResultVo.error(1501, "Oms属性不存在");

        // 创建分类关联关联
        OGoodsAttributeRelation relation = new OGoodsAttributeRelation();
        relation.setShopPlatformId(EnumShopType.IDOSELL.getIndex());
        relation.setAttributeId(omsAttributeId);
        relation.setShopAttributeId(attributeId);
        goodsAttributeRelationService.save(relation);
        log.info("=======创建属性关联======={}", JSONObject.toJSONString(relation));

        // 更新自己
        OmsIdosellSaleAttr update = new OmsIdosellSaleAttr();
        update.setAttributeId(attributeId);
        update.setOmsAttributeId(omsAttributeId);
        osIdosellSaleAttrMapper.updateById(update);

        return ResultVo.success();

    }
}




