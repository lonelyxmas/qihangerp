package cn.qihangerp.open.shein.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.module.open.shein.domain.OmsSheinGoodsSkuStock;
import cn.qihangerp.module.open.shein.domain.bo.SheinGoodsStockBo;
import cn.qihangerp.module.open.tao.domain.bo.TaoGoodsBo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【oms_shein_goods_sku_stock(库存信息表)】的数据库操作Service
* @createDate 2025-03-10 18:48:40
*/
public interface OmsSheinGoodsSkuStockService extends IService<OmsSheinGoodsSkuStock> {
    PageResult<OmsSheinGoodsSkuStock> queryPageList(SheinGoodsStockBo bo, PageQuery pageQuery);
    ResultVo pushStockToOms(Long id);
}
