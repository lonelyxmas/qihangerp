package cn.qihangerp.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.goods.domain.OGoodsPublish;
import cn.qihangerp.oms.service.OGoodsPublishService;
import cn.qihangerp.module.goods.mapper.OGoodsPublishMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author qilip
* @description 针对表【o_goods_publish(商品发布情况)】的数据库操作Service实现
* @createDate 2025-03-14 16:38:25
*/
@AllArgsConstructor
@Service
public class OGoodsPublishServiceImpl extends ServiceImpl<OGoodsPublishMapper, OGoodsPublish>
    implements OGoodsPublishService{
    private final OGoodsPublishMapper oGoodsPublishMapper;

    @Override
    public List<OGoodsPublish> getPulishListByGoodsId(Long goodsId) {
        LambdaQueryWrapper<OGoodsPublish> queryWrapper = new LambdaQueryWrapper<OGoodsPublish>()
                .eq(OGoodsPublish::getGoodsId,goodsId);
        return oGoodsPublishMapper.selectList(queryWrapper);
    }
}




