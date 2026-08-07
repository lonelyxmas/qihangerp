package cn.qihangerp.open.shein.service;

import cn.qihangerp.common.ResultVo;

import java.util.List;
import java.util.Map;

public interface SheinGoodsCommonService {

    void pullProductAndSkuList(Long shopId,String appKey, String appSecret, String url) throws InterruptedException;
//    void pullProductList(String appKey, String appSecret, String url);

//    void pullProductSkuList(String appKey, String appSecret, String url);

    ResultVo publishToShein(Long goodsId, Long shopId);


//    void batchSyncGoods();

    void updateSheinGoodsStatus(String goodsId);

    Map<Long, Integer> selectSheinCheckStatus(List<String> list);

    void asyncSheinGoodsStatus(List<String> goodsIdList);
}
