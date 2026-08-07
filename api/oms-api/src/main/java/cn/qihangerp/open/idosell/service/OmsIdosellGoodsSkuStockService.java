package cn.qihangerp.open.idosell.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellGoodsSkuStock;
import cn.qihangerp.module.open.idosell.domain.bo.IdosellGoodsStockBo;
import cn.qihangerp.module.open.shein.domain.OmsSheinGoodsSkuStock;
import cn.qihangerp.module.open.shein.domain.bo.SheinGoodsStockBo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【oms_idosell_goods_sku_stock(IdoSell商品SKU库存表)】的数据库操作Service
* @createDate 2025-03-18 19:44:29
*/
public interface OmsIdosellGoodsSkuStockService extends IService<OmsIdosellGoodsSkuStock> {
    PageResult<OmsIdosellGoodsSkuStock> queryPageList(IdosellGoodsStockBo bo, PageQuery pageQuery);
    ResultVo pushStockToOms(Long id);
}
