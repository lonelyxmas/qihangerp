package cn.qihangerp.oms.openApi.shein.controller;

import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.module.open.idosell.domain.bo.OrderSearchParam;
import cn.qihangerp.module.open.shein.domain.OmsSheinPurchaseOrder;
import cn.qihangerp.open.shein.service.OmsSheinPurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/open-api/shein/order")
@RestController
@RequiredArgsConstructor
public class SheinOrderController extends BaseController {
    private final OmsSheinPurchaseOrderService orderService;
    /**
     * 列表
     * @param bo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo orderList(OrderSearchParam bo, PageQuery pageQuery) {
        PageResult<OmsSheinPurchaseOrder> result = orderService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }

}
