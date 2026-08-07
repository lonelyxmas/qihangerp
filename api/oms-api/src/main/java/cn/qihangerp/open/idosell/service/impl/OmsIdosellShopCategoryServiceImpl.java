package cn.qihangerp.open.idosell.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.module.goods.domain.OGoodsCategory;
import cn.qihangerp.module.goods.domain.OGoodsCategoryRelation;
import cn.qihangerp.module.goods.mapper.OGoodsCategoryMapper;
import cn.qihangerp.module.goods.mapper.OGoodsCategoryRelationMapper;
import cn.qihangerp.module.open.idosell.domain.bo.CategorySearchParam;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellShopCategory;
import cn.qihangerp.open.idosell.service.OmsIdosellShopCategoryService;
import cn.qihangerp.module.open.idosell.mapper.OmsIdosellShopCategoryMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
* @author qilip
* @description 针对表【oms_idosell_shop_category】的数据库操作Service实现
* @createDate 2025-03-12 10:39:50
*/
@Slf4j
@AllArgsConstructor
@Service
public class OmsIdosellShopCategoryServiceImpl extends ServiceImpl<OmsIdosellShopCategoryMapper, OmsIdosellShopCategory>
    implements OmsIdosellShopCategoryService{
    private final OmsIdosellShopCategoryMapper omsIdosellShopCategoryMapper;
    private final OGoodsCategoryRelationMapper oGoodsCategoryRelationMapper;
    private final OGoodsCategoryMapper oGoodsCategoryMapper;

    @Override
    public PageResult<OmsIdosellShopCategory> queryPageList(CategorySearchParam bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OmsIdosellShopCategory> queryWrapper = new LambdaQueryWrapper<OmsIdosellShopCategory>()
                .eq(bo.getShopId() != null, OmsIdosellShopCategory::getShopId, bo.getShopId())
                .eq(bo.getId() != null, OmsIdosellShopCategory::getId, bo.getId())
                .eq(StringUtils.hasText(bo.getName()), OmsIdosellShopCategory::getName, bo.getName());

        pageQuery.setOrderByColumn("product_count");
        pageQuery.setIsAsc("desc");
        Page<OmsIdosellShopCategory> goodsPage = omsIdosellShopCategoryMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(goodsPage);
    }

    @Override
    public void saveCategory(OmsIdosellShopCategory category) {
        OmsIdosellShopCategory category1 = omsIdosellShopCategoryMapper.selectById(category.getId());
        if(category1 != null){
            omsIdosellShopCategoryMapper.updateById(category);
            log.info("===========更新Idosell店铺分类========{}", JSONObject.toJSONString(category));
        }else{
            omsIdosellShopCategoryMapper.insert(category);
            log.info("===========添加Idosell店铺分类========{}", JSONObject.toJSONString(category));
        }
    }

    @Transactional
    @Override
    public ResultVo bindOmsCategory(Integer id, Long categoryId) {
        OmsIdosellShopCategory omsIdosellShopCategory = omsIdosellShopCategoryMapper.selectById(id);
        if(omsIdosellShopCategory == null){
            return ResultVo.error(1500,"数据不存在");
        }
        OGoodsCategory oGoodsCategory = oGoodsCategoryMapper.selectById(categoryId);
        if(oGoodsCategory==null) return ResultVo.error(1501,"Oms分类不存在");

        // 创建分类关联关联
        OGoodsCategoryRelation oGoodsCategoryRelation = new OGoodsCategoryRelation();
        oGoodsCategoryRelation.setShopPlatformId(EnumShopType.IDOSELL.getIndex());
        oGoodsCategoryRelation.setShopCategoryId(id.longValue());
        oGoodsCategoryRelation.setCategoryId(categoryId);
        oGoodsCategoryRelationMapper.insert(oGoodsCategoryRelation);
        log.info("=======创建分类关联======={}", JSONObject.toJSONString(oGoodsCategoryRelation));

        // 更新自己
        OmsIdosellShopCategory update = new OmsIdosellShopCategory();
        update.setId(id);
        update.setOmsCategoryId(categoryId);
        omsIdosellShopCategoryMapper.updateById(update);

        return ResultVo.success();
    }


}




