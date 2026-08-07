package cn.qihangerp.oms.openApi.idosell.controller;

import cn.qihangerp.oms.openApi.PullRequest;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.open.idosell.service.IIdosellGoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/open-api/idosell/goods")
@RestController
@RequiredArgsConstructor
public class IdosellGoodsApiController extends BaseController {

    private final IIdosellGoodsService idosellGoodsService;
    /**
     * 拉取商品列表（包含sku）
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public AjaxResult pullSkuList(@RequestBody(required = false) PullRequest request) throws Exception {
        if(request.getShopId() == null||request.getShopId()<=0) return AjaxResult.error("缺少参数：shopId");
        idosellGoodsService.importGoods(request.getShopId());
        return AjaxResult.success("接口拉取成功");
    }
}
