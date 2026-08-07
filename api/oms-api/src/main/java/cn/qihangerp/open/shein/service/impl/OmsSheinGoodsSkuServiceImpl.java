package cn.qihangerp.open.shein.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.module.open.tao.domain.bo.TaoGoodsBo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.open.shein.domain.OmsSheinGoodsSku;
import cn.qihangerp.open.shein.service.OmsSheinGoodsSkuService;
import cn.qihangerp.module.open.shein.mapper.OmsSheinGoodsSkuMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author qilip
* @description 针对表【oms_shein_goods_sku(shein商品sku表)】的数据库操作Service实现
* @createDate 2025-03-11 15:40:39
*/
@AllArgsConstructor
@Service
public class OmsSheinGoodsSkuServiceImpl extends ServiceImpl<OmsSheinGoodsSkuMapper, OmsSheinGoodsSku>
    implements OmsSheinGoodsSkuService{
    private final OmsSheinGoodsSkuMapper osSheinGoodsSkuMapper;
    @Override
    public PageResult<OmsSheinGoodsSku> queryPageList(TaoGoodsBo param, PageQuery pageQuery) {
        LambdaQueryWrapper<OmsSheinGoodsSku> queryWrapper = new LambdaQueryWrapper<OmsSheinGoodsSku>();
        var page = osSheinGoodsSkuMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(page);
    }
}




