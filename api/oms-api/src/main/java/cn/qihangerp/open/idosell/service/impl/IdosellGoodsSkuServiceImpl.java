package cn.qihangerp.open.idosell.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.module.open.idosell.domain.bo.IdosellGoodsStockBo;
import cn.qihangerp.module.open.idosell.domain.vo.IdosellGoodsSku;
import cn.qihangerp.module.open.idosell.mapper.IdosellGoodsSkuMapper;
import cn.qihangerp.open.idosell.service.IIdosellGoodsSkuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * IdoSell商品SKU表 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2025-02-08
 */
@AllArgsConstructor
@Service
public class IdosellGoodsSkuServiceImpl extends ServiceImpl<IdosellGoodsSkuMapper, IdosellGoodsSku> implements IIdosellGoodsSkuService {
    private final IdosellGoodsSkuMapper idosellGoodsSkuMapper;
    @Override
    public PageResult<IdosellGoodsSku> queryPageList(IdosellGoodsStockBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<IdosellGoodsSku> queryWrapper = new LambdaQueryWrapper<IdosellGoodsSku>();
        queryWrapper.eq(bo.getProductId()!=null, IdosellGoodsSku::getProductId, bo.getProductId());
//        queryWrapper.eq(StringUtils.hasText(bo.getSkuId()), IdosellGoodsSku::getSkuId, bo.getSkuId());
        queryWrapper.eq(StringUtils.hasText(bo.getGoodsNum()), IdosellGoodsSku::getGoodsNum, bo.getGoodsNum());
        Page<IdosellGoodsSku> goodsSkuPage = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(goodsSkuPage);
    }
}
