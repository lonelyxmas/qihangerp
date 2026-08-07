package cn.qihangerp.oms.service;

import cn.qihangerp.module.goods.domain.OGoodsSku;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author qilip
* @description 针对表【o_goods_sku(OMS商品SKU表)】的数据库操作Service
* @createDate 2025-03-14 11:54:51
*/
public interface OGoodsSkuService extends IService<OGoodsSku> {
    List<OGoodsSku> searchGoodsSpec(String keyword);
}
