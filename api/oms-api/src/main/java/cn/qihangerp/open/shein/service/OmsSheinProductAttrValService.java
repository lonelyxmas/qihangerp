package cn.qihangerp.open.shein.service;

import cn.qihangerp.module.open.idosell.domain.OmsIdosellSaleAttrVal;
import cn.qihangerp.module.open.shein.domain.OmsSheinProductAttrVal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author qilip
* @description 针对表【oms_shein_product_type_attr_val】的数据库操作Service
* @createDate 2025-03-12 14:49:22
*/
public interface OmsSheinProductAttrValService extends IService<OmsSheinProductAttrVal> {
    List<OmsSheinProductAttrVal> getValueByAttributeId(Long attribute);
}
