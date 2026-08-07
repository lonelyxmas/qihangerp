package cn.qihangerp.oms.openApi.idosell.controller;

import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellOrder;
import cn.qihangerp.module.open.idosell.domain.bo.OrderSearchParam;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.open.idosell.service.OmsIdosellOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/open-api/idosell/order")
@RestController
@RequiredArgsConstructor
public class IdosellOrderController extends BaseController {
    private final OmsIdosellOrderService orderService;
    /**
     * 列表
     * @param bo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo orderList(OrderSearchParam bo, PageQuery pageQuery) {
        PageResult<OmsIdosellOrder> result = orderService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }

}
