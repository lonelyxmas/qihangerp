package cn.qihangerp.open.shein.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.module.goods.domain.*;
import cn.qihangerp.module.goods.mapper.*;
import cn.qihangerp.oms.service.OGoodsCategoryService;
import cn.qihangerp.module.open.shein.domain.OmsSheinProductAttr;
import cn.qihangerp.module.open.shein.domain.OmsSheinProductTypeAttr;
import cn.qihangerp.module.open.shein.domain.OmsSheinProductAttrVal;
import cn.qihangerp.open.shein.request.CategoryRequest;
import cn.qihangerp.open.shein.service.OmsSheinProductAttrService;
import cn.qihangerp.open.shein.service.OmsSheinProductTypeAttrService;
import cn.qihangerp.open.shein.service.OmsSheinProductAttrValService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.open.shein.domain.OmsSheinShopCategory;
import cn.qihangerp.open.shein.service.OmsSheinShopCategoryService;
import cn.qihangerp.module.open.shein.mapper.OmsSheinShopCategoryMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
* @author qilip
* @description 针对表【oms_shein_shop_category】的数据库操作Service实现
* @createDate 2025-03-12 11:12:54
*/
@Slf4j
@AllArgsConstructor
@Service
public class OmsSheinShopCategoryServiceImpl extends ServiceImpl<OmsSheinShopCategoryMapper, OmsSheinShopCategory>
    implements OmsSheinShopCategoryService{
    private final OmsSheinShopCategoryMapper omsSheinShopCategoryMapper;
    private final OmsSheinProductAttrService productAttrService;
    private final OmsSheinProductAttrValService productAttrValService;
    private final OmsSheinProductTypeAttrService productTypeAttrService;
    private final OGoodsCategoryService oGoodsCategoryService;
    private final OGoodsAttributeMapper oGoodsAttributeMapper;
    private final OGoodsAttributeValueMapper oGoodsAttributeValueMapper;
    private final OGoodsCategoryAttributeMapper oGoodsCategoryAttributeMapper;
    private final OGoodsCategoryRelationMapper oGoodsCategoryRelationMapper;
    private final OGoodsAttributeRelationMapper oGoodsAttributeRelationMapper;
    private final OGoodsAttributeValueRelationMapper oGoodsAttributeValueRelationMapper;
    @Override
    public PageResult<OmsSheinShopCategory> queryPageList(CategoryRequest bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OmsSheinShopCategory> queryWrapper = new LambdaQueryWrapper<OmsSheinShopCategory>()
                .eq(bo.getShopId() != null, OmsSheinShopCategory::getShopId, bo.getShopId())
                .eq(bo.getCategoryId() != null, OmsSheinShopCategory::getCategoryId, bo.getCategoryId())
                .eq(bo.getProductTypeId() != null, OmsSheinShopCategory::getProductTypeId, bo.getProductTypeId())
                ;


        pageQuery.setOrderByColumn("category_id");
        pageQuery.setIsAsc("desc");
        Page<OmsSheinShopCategory> goodsPage = omsSheinShopCategoryMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(goodsPage);
    }

    @Override
    public void saveCategory(OmsSheinShopCategory category) {
        OmsSheinShopCategory category1 = omsSheinShopCategoryMapper.selectById(category.getCategoryId());
        if(category1 != null){
            omsSheinShopCategoryMapper.updateById(category);
            log.info("===========更新Shein店铺分类========{}", JSONObject.toJSONString(category));
        }else{
            omsSheinShopCategoryMapper.insert(category);
            log.info("===========添加Shein店铺分类========{}", JSONObject.toJSONString(category));
        }
    }


    @Override
    public void batchPushToOms() {
        log.info("开始同步Shein分类及属性");
        if(oGoodsCategoryService.list().size()>0){
            log.info("存在分类，不允许推送，请清空分类及分类属性后才能进行推送操作");
            throw new RuntimeException("存在分类，不允许推送，请清空分类及分类属性后才能进行推送操作");
        }
//        // 1、先同步分类属性
//        log.info("=======循环同步分类属性=======");
//        // 分类属性List
//        List<OmsSheinProductTypeAttr> productTypeAttrs = productTypeAttrService.list();
//        Map<Long, OmsSheinProductTypeAttr> filteredMap = productTypeAttrs.stream()
//                .collect(Collectors.toMap(
//                        OmsSheinProductTypeAttr::getAttributeId,  // 键：attribute_id
//                        Function.identity(),  // 值：当前对象
//                        (existing, replacement) -> existing  // 如果有重复键，保留第一个
//                ));
//        //得到去重后的属性List
//        List<OmsSheinProductTypeAttr> attrs = new ArrayList<>(filteredMap.values());
//        // 属性值List
//        List<OmsSheinProductTypeAttrVal> attrVals = productTypeAttrValService.list();
////        for(OmsSheinProductTypeAttrVal attrVal : attrVals){
////            OGoodsAttributeValue value = new OGoodsAttributeValue();
////            value.setAttributeValueId(attrVal.getAttributeValueId());
////            value.set
////        }
//        for(var attr : productTypeAttrs){
//
//            // 1、插入到分类属性o_goods_category_attribute
//
//        }

        // 同步分类
        List<OmsSheinShopCategory> topCategories = omsSheinShopCategoryMapper.selectList(new LambdaQueryWrapper<OmsSheinShopCategory>().eq(OmsSheinShopCategory::getParentCategoryId,0));
        log.info("=======循环同步分类数据======={}",JSONObject.toJSONString(topCategories));
        for(OmsSheinShopCategory topCategory : topCategories) {
            syncCategoryAll(topCategory);
        }
        log.info("=======循环同步分类数据结束=======");

    }
    @Transactional
    protected void syncCategoryAll(OmsSheinShopCategory topCategory) {

        OGoodsCategory cate = new OGoodsCategory();
        cate.setId(topCategory.getCategoryId());
        cate.setNumber(topCategory.getCategoryId().toString());
        cate.setName(topCategory.getCategoryName());
        cate.setParentId(topCategory.getParentCategoryId());
        cate.setProductTypeId(topCategory.getProductTypeId());
        cate.setCreateBy("Shein同步");
        cate.setCreateTime(new Date());
        oGoodsCategoryService.save(cate);
        log.info("=======同步分类======={}", JSONObject.toJSONString(cate));
        // 创建分类关联关联
        OGoodsCategoryRelation oGoodsCategoryRelation = new OGoodsCategoryRelation();
        oGoodsCategoryRelation.setShopPlatformId(EnumShopType.SHEIN.getIndex());
        oGoodsCategoryRelation.setShopCategoryId(topCategory.getCategoryId());
        oGoodsCategoryRelation.setCategoryId(cate.getId());
        oGoodsCategoryRelationMapper.insert(oGoodsCategoryRelation);
        log.info("=======创建分类关联======={}", JSONObject.toJSONString(oGoodsCategoryRelation));

        //更新自己
        OmsSheinShopCategory update = new OmsSheinShopCategory();
        update.setCategoryId(topCategory.getCategoryId());
        update.setOmsCategoryId(cate.getId());
        omsSheinShopCategoryMapper.updateById(update);
        // 递归同步子类
        syncCategories(topCategory.getCategoryId());

    }

    private void syncCategories(Long parentCategoryId) {
        // 查询当前父类下的所有子类
        List<OmsSheinShopCategory> childCategories = omsSheinShopCategoryMapper.selectList(
                new LambdaQueryWrapper<OmsSheinShopCategory>().eq(OmsSheinShopCategory::getParentCategoryId, parentCategoryId)
        );

        // 遍历当前父类下的所有子类
        for (OmsSheinShopCategory shopCategory : childCategories) {
            // 创建并保存OGoodsCategory对象
            OGoodsCategory cate = new OGoodsCategory();
            cate.setId(shopCategory.getCategoryId());
            cate.setNumber(shopCategory.getCategoryId().toString());
            cate.setName(shopCategory.getCategoryName());
            cate.setParentId(shopCategory.getParentCategoryId());
            cate.setProductTypeId(shopCategory.getProductTypeId());
            cate.setCreateBy("Shein同步");
            cate.setCreateTime(new Date());
            oGoodsCategoryService.save(cate);
            log.info("=======同步当前分类======={}", JSONObject.toJSONString(shopCategory));
            // 创建分类关联关联
            OGoodsCategoryRelation oGoodsCategoryRelation = new OGoodsCategoryRelation();
            oGoodsCategoryRelation.setShopPlatformId(EnumShopType.SHEIN.getIndex());
            oGoodsCategoryRelation.setShopCategoryId(shopCategory.getCategoryId());
            oGoodsCategoryRelation.setCategoryId(cate.getId());
            oGoodsCategoryRelationMapper.insert(oGoodsCategoryRelation);
            log.info("=======创建分类关联======={}", JSONObject.toJSONString(oGoodsCategoryRelation));
            //更新自己
            OmsSheinShopCategory update = new OmsSheinShopCategory();
            update.setCategoryId(shopCategory.getCategoryId());
            update.setOmsCategoryId(cate.getId());
            omsSheinShopCategoryMapper.updateById(update);

            // 插入分类属性
            if(shopCategory.getProductTypeId()!=null && shopCategory.getProductTypeId()>0){
                log.info("=========同步分类属性=============");
                // 1、查出分类属性
                List<OmsSheinProductTypeAttr> productTypeAttrList = productTypeAttrService.list(
                        new LambdaQueryWrapper<OmsSheinProductTypeAttr>()
                        .eq(OmsSheinProductTypeAttr::getProductTypeId, shopCategory.getProductTypeId()));
                if(productTypeAttrList!=null && productTypeAttrList.size()>0){
                    for (var typeAttr:productTypeAttrList) {
                        // 插入attr
                        OGoodsAttribute exist = oGoodsAttributeMapper.selectById(typeAttr.getAttributeId());
                        if(exist==null){
                            OGoodsAttribute goodsAttribute = new OGoodsAttribute();
                            goodsAttribute.setAttributeId(typeAttr.getAttributeId());
                            goodsAttribute.setAttributeName(typeAttr.getAttributeName());
                            goodsAttribute.setAttributeType(typeAttr.getAttributeType());
                            goodsAttribute.setAttributeMode(typeAttr.getAttributeMode());
                            goodsAttribute.setAttributeStatus(typeAttr.getAttributeStatus());
                            goodsAttribute.setAttributeLabel(typeAttr.getAttributeLabel());
                            oGoodsAttributeMapper.insert(goodsAttribute);
                            log.info("====插入属性===={}",JSONObject.toJSONString(goodsAttribute));
                            // 创建属性关联
                            OGoodsAttributeRelation oGoodsAttributeRelation = new OGoodsAttributeRelation();
                            oGoodsAttributeRelation.setShopPlatformId(EnumShopType.SHEIN.getIndex());
                            oGoodsAttributeRelation.setShopAttributeId(typeAttr.getAttributeId());
                            oGoodsAttributeRelation.setAttributeId(goodsAttribute.getAttributeId());
                            oGoodsAttributeRelationMapper.insert(oGoodsAttributeRelation);
                            // 更新自己
                            OmsSheinProductAttr productAttr = productAttrService.getById(typeAttr.getAttributeId());
                            if(productAttr!=null){
                                productAttr.setOmsAttributeId(goodsAttribute.getAttributeId());
                                productAttrService.updateById(productAttr);
                            }
                        }

                        // 插入attr_value
                        List<OmsSheinProductAttrVal> attrVals = productAttrValService.list(
                                new LambdaQueryWrapper<OmsSheinProductAttrVal>()
                                        .eq(OmsSheinProductAttrVal::getAttributeId,typeAttr.getAttributeId()));
                        for(var attrVal:attrVals){
                            OGoodsAttributeValue exits = oGoodsAttributeValueMapper.selectById(attrVal.getAttributeValueId());
                            if(exits==null){
                                OGoodsAttributeValue goodsAttributeValue = new OGoodsAttributeValue();
                                goodsAttributeValue.setAttributeId(typeAttr.getAttributeId());
                                goodsAttributeValue.setAttributeValue(attrVal.getAttributeValue());
                                goodsAttributeValue.setAttributeValueId(attrVal.getAttributeValueId());
                                oGoodsAttributeValueMapper.insert(goodsAttributeValue);
                                log.info("====插入属性值===={}",JSONObject.toJSONString(goodsAttributeValue));

                                // 创建属性值关联
                                OGoodsAttributeValueRelation relation = new OGoodsAttributeValueRelation();
                                relation.setShopPlatformId(EnumShopType.SHEIN.getIndex());
                                relation.setShopAttributeValueId(attrVal.getAttributeValueId());
                                relation.setAttributeValueId(typeAttr.getAttributeId());
                                oGoodsAttributeValueRelationMapper.insert(relation);
                                // 更新自己
                                attrVal.setOmsAttributeValueId(goodsAttributeValue.getAttributeValueId());
                                productAttrValService.updateById(attrVal);
                            }

                        }

                        // 插入分类属性
                        OGoodsCategoryAttribute categoryAttribute = new OGoodsCategoryAttribute();
                        categoryAttribute.setCategoryId(cate.getId());
                        categoryAttribute.setAttributeId(typeAttr.getAttributeId());
                        categoryAttribute.setAttributeName(typeAttr.getAttributeName());
                        categoryAttribute.setAttributeType(typeAttr.getAttributeType());
                        categoryAttribute.setAttributeLabel(typeAttr.getAttributeLabel());
                        categoryAttribute.setAttributeStatus(typeAttr.getAttributeStatus());
                        categoryAttribute.setAttributeMode(typeAttr.getAttributeMode());
                        categoryAttribute.setAttributeIsShow(typeAttr.getAttributeIsShow());
                        categoryAttribute.setCreateTime(new Date());
                        oGoodsCategoryAttributeMapper.insert(categoryAttribute);
                        log.info("====插入分类属性===={}",JSONObject.toJSONString(categoryAttribute));
                    }
                }

            }
            // 递归同步当前分类的子分类
            syncCategories(shopCategory.getCategoryId());
        }
    }

}




