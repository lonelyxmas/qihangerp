package cn.qihangerp.open.shein.service.impl;

import cn.qihangerp.module.open.shein.domain.OmsSheinProductAttr;
import cn.qihangerp.module.open.shein.domain.OmsSheinProductAttrVal;
import cn.qihangerp.module.open.shein.mapper.OmsSheinProductAttrMapper;
import cn.qihangerp.module.open.shein.mapper.OmsSheinProductAttrValMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.open.shein.domain.OmsSheinProductTypeAttr;
import cn.qihangerp.open.shein.service.OmsSheinProductTypeAttrService;
import cn.qihangerp.module.open.shein.mapper.OmsSheinProductTypeAttrMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
* @author qilip
* @description 针对表【oms_shein_product_type_attr】的数据库操作Service实现
* @createDate 2025-03-12 15:23:46
*/
@Slf4j
@AllArgsConstructor
@Service
public class OmsSheinProductTypeAttrServiceImpl extends ServiceImpl<OmsSheinProductTypeAttrMapper, OmsSheinProductTypeAttr>
    implements OmsSheinProductTypeAttrService{
    private final OmsSheinProductTypeAttrMapper typeAttrMapper;
    private final OmsSheinProductAttrValMapper attrValMapper;
    private final OmsSheinProductAttrMapper attrMapper;

    @Transactional
    @Override
    public void saveAttr(OmsSheinProductTypeAttr attr) {
        List<OmsSheinProductTypeAttr> attrs = typeAttrMapper.selectList(new LambdaQueryWrapper<OmsSheinProductTypeAttr>()
                .eq(OmsSheinProductTypeAttr::getProductTypeId,attr.getProductTypeId())
                .eq(OmsSheinProductTypeAttr::getAttributeId,attr.getAttributeId())
        );
        if(attrs != null&&attrs.size()>0){
//            attr.setUpdateTime(new Date());
//            attrMapper.updateById(attr);
            log.info("====分类属性已存在====={}",attr.getAttributeId());
        }else {
            attr.setCreateTime(new Date());
            typeAttrMapper.insert(attr);
            log.info("====添加分类属性====={}",attr.getAttributeId());
        }
        // 插入属性表oms_shein_product_attr
        OmsSheinProductAttr productAttr = attrMapper.selectById(attr.getAttributeId());
        if(productAttr==null){
            log.info("====属性不存在，新增====={}",attr.getAttributeId());
            OmsSheinProductAttr productAttr1 = new OmsSheinProductAttr();
            productAttr1.setAttributeId(attr.getAttributeId());
            productAttr1.setAttributeName(attr.getAttributeName());
            productAttr1.setAttributeType(attr.getAttributeType());
            productAttr1.setAttributeLabel(attr.getAttributeLabel());
            productAttr1.setAttributeStatus(attr.getAttributeStatus());
            productAttr1.setAttributeMode(attr.getAttributeMode());
            productAttr1.setAttributeIsShow(attr.getAttributeIsShow());
            attrMapper.insert(productAttr1);
        }
        if(attr.getVals()!=null){
            for(var val:attr.getVals()){
                List<OmsSheinProductAttrVal> attrVals = attrValMapper.selectList(
                        new LambdaQueryWrapper<OmsSheinProductAttrVal>()
                                .eq(OmsSheinProductAttrVal::getAttributeValueId, val.getAttributeValueId())
                                .eq(OmsSheinProductAttrVal::getAttributeId, attr.getAttributeId()));
                if(attrVals != null&&!attrVals.isEmpty()){
                    // 更新
//                    val.setId(attrVals.get(0).getId());
//                    attrValMapper.updateById(val);
                    log.info("====分类属性值已存在====={}",val.getAttributeValue());
                }else {
                    attrValMapper.insert(val);
                    log.info("====添加分类属性值====={}",val.getAttributeValue());
                }
            }
        }
    }

    @Override
    public OmsSheinProductTypeAttr getAttrByProductTypeId(Long productTypeId) {
        var attrList = typeAttrMapper.selectList(new LambdaQueryWrapper<OmsSheinProductTypeAttr>().eq(OmsSheinProductTypeAttr::getProductTypeId,productTypeId));
        if(attrList.isEmpty()) return null;
        else {
            OmsSheinProductTypeAttr attr = attrList.get(0);
            attr.setVals(attrValMapper.selectList(new LambdaQueryWrapper<OmsSheinProductAttrVal>().eq(OmsSheinProductAttrVal::getAttributeId,attr.getAttributeId())));
            return attr;
        }
    }
}




