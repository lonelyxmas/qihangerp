package cn.qihangerp.open.idosell.service;

import cn.qihangerp.common.ResultVo;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellSaleAttrVal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author qilip
* @description 针对表【oms_idosell_sale_attr_val(idosell销售属性值)】的数据库操作Service
* @createDate 2025-03-13 20:52:43
*/
public interface OmsIdosellSaleAttrValService extends IService<OmsIdosellSaleAttrVal> {
    List<OmsIdosellSaleAttrVal> getValueByAttributeId(Long attribute);
    ResultVo bindOmsAttributeValue(Long attributeValueId, Long omsAttributeValueId);
}
