package cn.qihangerp.open.idosell.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.module.open.idosell.domain.bo.IdosellGoodsStockBo;
import cn.qihangerp.module.open.idosell.domain.vo.IdosellGoodsSku;
import cn.qihangerp.module.open.shein.domain.OmsSheinGoodsSkuStock;
import cn.qihangerp.module.open.tao.domain.bo.TaoGoodsBo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * IdoSell商品SKU表 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2025-02-08
 */
public interface IIdosellGoodsSkuService extends IService<IdosellGoodsSku> {
    PageResult<IdosellGoodsSku> queryPageList(IdosellGoodsStockBo bo, PageQuery pageQuery);
}
