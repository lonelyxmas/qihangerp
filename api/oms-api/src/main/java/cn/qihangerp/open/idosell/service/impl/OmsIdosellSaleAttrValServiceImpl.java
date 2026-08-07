package cn.qihangerp.open.idosell.service.impl;

import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.module.goods.domain.OGoodsAttributeValueRelation;
import cn.qihangerp.module.goods.mapper.OGoodsAttributeValueRelationMapper;
import cn.qihangerp.oms.service.OGoodsAttributeValueService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellSaleAttrVal;
import cn.qihangerp.open.idosell.service.OmsIdosellSaleAttrValService;
import cn.qihangerp.module.open.idosell.mapper.OmsIdosellSaleAttrValMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author qilip
* @description 针对表【oms_idosell_sale_attr_val(idosell销售属性值)】的数据库操作Service实现
* @createDate 2025-03-13 20:52:43
*/
@Slf4j
@AllArgsConstructor
@Service
public class OmsIdosellSaleAttrValServiceImpl extends ServiceImpl<OmsIdosellSaleAttrValMapper, OmsIdosellSaleAttrVal>
    implements OmsIdosellSaleAttrValService{
    private final OmsIdosellSaleAttrValMapper saleAttrValMapper;
    private final OGoodsAttributeValueService goodsAttributeValueService;
    private final OGoodsAttributeValueRelationMapper goodsAttributeValueRelationMapper;

    @Override
    public List<OmsIdosellSaleAttrVal> getValueByAttributeId(Long attributeId) {
        List<OmsIdosellSaleAttrVal> vals = saleAttrValMapper.selectList(new LambdaQueryWrapper<OmsIdosellSaleAttrVal>().eq(OmsIdosellSaleAttrVal::getAttributeId, attributeId));
        return vals;
    }

    @Override
    public ResultVo bindOmsAttributeValue(Long attributeValueId, Long omsAttributeValueId) {
        var attrVal = saleAttrValMapper.selectById(attributeValueId);
        if (attrVal == null) {
            return ResultVo.error(1500, "数据不存在");
        }
        var omsAttrVal = goodsAttributeValueService.getById(omsAttributeValueId);
        if (omsAttrVal == null) return ResultVo.error(1501, "Oms属性值不存在");

        // 创建分类关联关联
        OGoodsAttributeValueRelation relation = new OGoodsAttributeValueRelation();
        relation.setShopPlatformId(EnumShopType.IDOSELL.getIndex());
        relation.setAttributeValueId(omsAttributeValueId);
        relation.setShopAttributeValueId(attributeValueId);
        goodsAttributeValueRelationMapper.insert(relation);
        log.info("=======创建属性关联======={}", JSONObject.toJSONString(relation));

        // 更新自己
        OmsIdosellSaleAttrVal update = new OmsIdosellSaleAttrVal();
        update.setAttributeValueId(attributeValueId);
        update.setOmsAttributeValueId(omsAttributeValueId);
        saleAttrValMapper.updateById(update);

        return ResultVo.success();
    }
}




