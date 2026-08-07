package cn.qihangerp.open.shein.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellOrder;
import cn.qihangerp.module.open.idosell.domain.bo.OrderSearchParam;
import cn.qihangerp.module.open.shein.domain.OmsSheinPurchaseOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【oms_shein_purchase_order(SHEIN采购订单主表)】的数据库操作Service
* @createDate 2025-03-10 12:24:02
*/
public interface OmsSheinPurchaseOrderService extends IService<OmsSheinPurchaseOrder> {
    PageResult<OmsSheinPurchaseOrder> queryPageList(OrderSearchParam param, PageQuery pageQuery);
    /**
     * 保存店铺订单
     * @param shopId
     * @param order
     * @return
     */
    ResultVo<Long> saveOrder(Long shopId, OmsSheinPurchaseOrder order);
}
