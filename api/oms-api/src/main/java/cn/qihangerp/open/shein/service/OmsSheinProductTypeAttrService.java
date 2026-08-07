package cn.qihangerp.open.shein.service;

import cn.qihangerp.module.open.shein.domain.OmsSheinProductTypeAttr;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【oms_shein_product_type_attr】的数据库操作Service
* @createDate 2025-03-12 15:23:46
*/
public interface OmsSheinProductTypeAttrService extends IService<OmsSheinProductTypeAttr> {
    void saveAttr(OmsSheinProductTypeAttr attr);
    OmsSheinProductTypeAttr getAttrByProductTypeId(Long productTypeId);
}
