package cn.qihangerp.open.shein.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.open.shein.domain.OmsSheinProductAttrVal;
import cn.qihangerp.open.shein.service.OmsSheinProductAttrValService;
import cn.qihangerp.module.open.shein.mapper.OmsSheinProductAttrValMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author qilip
* @description 针对表【oms_shein_product_type_attr_val】的数据库操作Service实现
* @createDate 2025-03-12 14:49:22
*/
@AllArgsConstructor
@Service
public class OmsSheinProductAttrValServiceImpl extends ServiceImpl<OmsSheinProductAttrValMapper, OmsSheinProductAttrVal>
    implements OmsSheinProductAttrValService {
    private final OmsSheinProductAttrValMapper attrValMapper;

    @Override
    public List<OmsSheinProductAttrVal> getValueByAttributeId(Long attributeId) {
        List<OmsSheinProductAttrVal> vals = attrValMapper.selectList(new LambdaQueryWrapper<OmsSheinProductAttrVal>().eq(OmsSheinProductAttrVal::getAttributeId, attributeId));
        return vals;
    }
}




