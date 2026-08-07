package cn.qihangerp.open.shein.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.module.open.shein.domain.OmsSheinGoods;
import cn.qihangerp.open.shein.request.SheinGoodsBo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【oms_shein_goods(shein商品表)】的数据库操作Service
* @createDate 2025-03-11 15:23:49
*/
public interface OmsSheinGoodsService extends IService<OmsSheinGoods> {
    PageResult<OmsSheinGoods> queryPageList(SheinGoodsBo param, PageQuery pageQuery);
    ResultVo sync(SheinGoodsBo bo);
    void batchSync();
}
