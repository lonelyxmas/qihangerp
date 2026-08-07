package cn.qihangerp.open.shein.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.module.open.shein.domain.OmsSheinGoods;
import cn.qihangerp.module.open.shein.domain.OmsSheinGoodsSku;
import cn.qihangerp.module.open.tao.domain.bo.TaoGoodsBo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【oms_shein_goods_sku(shein商品sku表)】的数据库操作Service
* @createDate 2025-03-11 15:40:39
*/
public interface OmsSheinGoodsSkuService extends IService<OmsSheinGoodsSku> {
    PageResult<OmsSheinGoodsSku> queryPageList(TaoGoodsBo param, PageQuery pageQuery);
}
