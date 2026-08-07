package cn.qihangerp.oms.service;

import cn.qihangerp.module.goods.domain.OGoodsPublish;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author qilip
* @description 针对表【o_goods_publish(商品发布情况)】的数据库操作Service
* @createDate 2025-03-14 16:38:25
*/
public interface OGoodsPublishService extends IService<OGoodsPublish> {
    List<OGoodsPublish> getPulishListByGoodsId(Long goodsId);
}
