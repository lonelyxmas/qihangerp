package cn.qihangerp.open.idosell.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.module.open.dou.domain.DouOrder;
import cn.qihangerp.module.open.dou.domain.bo.DouOrderBo;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellOrder;
import cn.qihangerp.module.open.idosell.domain.bo.OrderSearchParam;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【oms_idosell_order】的数据库操作Service
* @createDate 2025-03-09 18:36:37
*/
public interface OmsIdosellOrderService extends IService<OmsIdosellOrder> {
    PageResult<OmsIdosellOrder> queryPageList(OrderSearchParam param, PageQuery pageQuery);
    /**
     * 保存店铺订单
     * @param shopId
     * @param order
     * @return
     */
    ResultVo<Long> saveOrder(Long shopId, OmsIdosellOrder order);
}
