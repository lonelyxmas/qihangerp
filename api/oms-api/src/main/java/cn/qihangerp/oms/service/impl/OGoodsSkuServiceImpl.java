package cn.qihangerp.oms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.goods.domain.OGoodsSku;
import cn.qihangerp.oms.service.OGoodsSkuService;
import cn.qihangerp.module.goods.mapper.OGoodsSkuMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author qilip
* @description 针对表【o_goods_sku(OMS商品SKU表)】的数据库操作Service实现
* @createDate 2025-03-14 11:54:51
*/
@AllArgsConstructor
@Service
public class OGoodsSkuServiceImpl extends ServiceImpl<OGoodsSkuMapper, OGoodsSku>
    implements OGoodsSkuService{
    private final OGoodsSkuMapper skuMapper;
    @Override
    public List<OGoodsSku> searchGoodsSpec(String keyword) {
        LambdaQueryWrapper<OGoodsSku> queryWrapper =
                new LambdaQueryWrapper<OGoodsSku>()
                        .likeRight(OGoodsSku::getGoodsId,keyword).or()
                        .likeRight(OGoodsSku::getId,keyword).or()
                        .likeRight(OGoodsSku::getSkuCode,keyword).or()
                        .like(OGoodsSku::getGoodsName,keyword).or()
                        .like(OGoodsSku::getSkuName,keyword)
                ;
        queryWrapper.last("LIMIT 10");
        return skuMapper.selectList(queryWrapper);
    }
}




