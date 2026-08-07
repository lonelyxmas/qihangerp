package cn.qihangerp.open.idosell.service;

import cn.qihangerp.common.ResultVo;

public interface IdosellStockCommonService {

    void pullShopStockList(Long shopId,String appKey, String appSecret, String url);
    ResultVo pushShopStock(Long shopId,Long inventoryId);

}
