package cn.qihangerp.open.idosell.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellSaleAttr;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【oms_idosell_sale_attr(idosell销售属性)】的数据库操作Service
* @createDate 2025-03-13 20:52:43
*/
public interface OmsIdosellSaleAttrService extends IService<OmsIdosellSaleAttr> {
    PageResult<OmsIdosellSaleAttr> queryPageList(OmsIdosellSaleAttr bo, PageQuery pageQuery);
    ResultVo bindOmsAttribute(Long attributeId,Long omsAttributeId);
}
