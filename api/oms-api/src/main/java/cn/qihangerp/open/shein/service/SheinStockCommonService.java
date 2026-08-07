package cn.qihangerp.open.shein.service;

import cn.qihangerp.common.ResultVo;

import java.util.List;
import java.util.Map;

public interface SheinStockCommonService {

    void pullShopStockList(Long shopId,String appKey, String appSecret, String url);
    void pullSpuQuantity(Long shopId,String appKey, String appSecret, String url,List<String> spuName);
    ResultVo pushShopStock(Long shopId, Long inventoryId);
}
