package cn.qihangerp.open.idosell.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.module.goods.domain.OGoodsInventory;
import cn.qihangerp.module.goods.domain.OGoodsInventoryRecord;
import cn.qihangerp.module.goods.domain.OGoodsSku;
import cn.qihangerp.module.goods.mapper.OGoodsInventoryMapper;
import cn.qihangerp.module.goods.mapper.OGoodsInventoryRecordMapper;
import cn.qihangerp.oms.service.OGoodsSkuService;
import cn.qihangerp.module.open.idosell.domain.bo.IdosellGoodsStockBo;
import cn.qihangerp.module.open.idosell.domain.vo.IdosellGoodsSku;
import cn.qihangerp.open.idosell.service.IIdosellGoodsSkuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellGoodsSkuStock;
import cn.qihangerp.open.idosell.service.OmsIdosellGoodsSkuStockService;
import cn.qihangerp.module.open.idosell.mapper.OmsIdosellGoodsSkuStockMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
* @author qilip
* @description 针对表【oms_idosell_goods_sku_stock(IdoSell商品SKU库存表)】的数据库操作Service实现
* @createDate 2025-03-18 19:44:29
*/
@Slf4j
@AllArgsConstructor
@Service
public class OmsIdosellGoodsSkuStockServiceImpl extends ServiceImpl<OmsIdosellGoodsSkuStockMapper, OmsIdosellGoodsSkuStock>
    implements OmsIdosellGoodsSkuStockService{
    private final IIdosellGoodsSkuService idosellGoodsSkuService;
    private final OGoodsInventoryMapper oGoodsInventoryMapper;
    private final OGoodsInventoryRecordMapper oGoodsInventoryRecordMapper;
    private final OGoodsSkuService oGoodsSkuService;
    /**
     * 分页查询
     * @param bo
     * @param pageQuery
     * @return
     */
    @Override
    public PageResult<OmsIdosellGoodsSkuStock> queryPageList(IdosellGoodsStockBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OmsIdosellGoodsSkuStock> queryWrapper = new LambdaQueryWrapper<OmsIdosellGoodsSkuStock>();
        queryWrapper.eq(StringUtils.hasText(bo.getGoodsNum()), OmsIdosellGoodsSkuStock::getGoodsNum, bo.getGoodsNum());
        queryWrapper.eq(bo.getProductId()!=null, OmsIdosellGoodsSkuStock::getProductId, bo.getProductId());

        queryWrapper.eq(StringUtils.hasText(bo.getSkuCode()),OmsIdosellGoodsSkuStock::getSkuCode, bo.getSkuCode());

        Page<OmsIdosellGoodsSkuStock> sheinGoodsSkuPage = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(sheinGoodsSkuPage);
    }

    @Transactional
    @Override
    public ResultVo pushStockToOms(Long id) {
        OmsIdosellGoodsSkuStock skuStock = this.baseMapper.selectById(id);
        if(skuStock==null){
            log.error("没有找到库存信息");
            return ResultVo.error("Data Error");
        }
        // 查找商品数据 productId+sizeId
        List<IdosellGoodsSku> goodsSkuList = idosellGoodsSkuService.list(
                new LambdaQueryWrapper<IdosellGoodsSku>()
                        .eq(IdosellGoodsSku::getProductId, skuStock.getProductId())
                        .eq(IdosellGoodsSku::getSizeId, skuStock.getSizeId())
        );
        if(goodsSkuList==null||goodsSkuList.size()==0){
            log.error("没有找到Idosell Goods Sku：productId：{},sizeId：{}",skuStock.getProductId(),skuStock.getSizeId());
            return ResultVo.error("Data Error：Not Found Goods Sku");
        } else if (goodsSkuList.get(0).getOGoodsSkuId()==null||goodsSkuList.get(0).getOGoodsSkuId()==0) {
            log.error("Idosell Goods Sku没有关联OMS商品SKU：productId：{},sizeId：{}",skuStock.getProductId(),skuStock.getSizeId());
            return ResultVo.error("Data Error：Not Found OMS Goods Sku");
        }
        OGoodsSku oGoodsSku = oGoodsSkuService.getById(goodsSkuList.get(0).getOGoodsSkuId());
        if(oGoodsSku==null){
            log.error("没有找到OMS商品SKU：{}",goodsSkuList.get(0).getOGoodsSkuId());
            return ResultVo.error("Data Error：Not Found OMS Goods Sku");
        }

        // 3. 创建或更新库存记录
        OGoodsInventory existingInventory = oGoodsInventoryMapper.selectOne(
                new LambdaQueryWrapper<OGoodsInventory>()
                        .eq(OGoodsInventory::getSkuId, oGoodsSku.getId())
        );
        if(existingInventory==null){
            log.info("========商品库存不存在，初始化库存数据：{}============",oGoodsSku.getId());
            // 初始化库存
            OGoodsInventory inventory = new OGoodsInventory();
            inventory.setSkuId(oGoodsSku.getId());
            inventory.setGoodsId(oGoodsSku.getGoodsId());
            inventory.setGoodsNum(oGoodsSku.getGoodsNum());
            inventory.setSkuCode(oGoodsSku.getSkuCode());
            inventory.setGoodsName(oGoodsSku.getGoodsName());
            inventory.setColorImage(oGoodsSku.getColorImage());
            inventory.setColorValue(oGoodsSku.getColorValue());
            inventory.setSizeValue(oGoodsSku.getSizeValue());
            inventory.setQuantity(goodsSkuList.get(0).getStockQuantity()); // 使用IdoSell的库存数量
            inventory.setIsDelete(0);
            inventory.setCreateTime(new Date());
            inventory.setCreateBy("IdoSell同步");
            oGoodsInventoryMapper.insert(inventory);
            log.info("库存信息新增成功, skuId: {}", oGoodsSku.getId());

            // 增加库存入库记录
            OGoodsInventoryRecord inventoryRecord = new OGoodsInventoryRecord();
            inventoryRecord.setInventoryId(inventory.getId());
            inventoryRecord.setGoodsId(oGoodsSku.getGoodsId());
            inventoryRecord.setGoodsNum(oGoodsSku.getGoodsNum());
            inventoryRecord.setSkuId(oGoodsSku.getId());
            inventoryRecord.setSkuCode(oGoodsSku.getSkuCode());
            inventoryRecord.setBatchId(0L);
            inventoryRecord.setType(1);
            inventoryRecord.setInventoryDetailId(0L);
            inventoryRecord.setQuantity(inventory.getQuantity().intValue());
            inventoryRecord.setBalanceQuantity(inventoryRecord.getQuantity());
            inventoryRecord.setLockedQuantity(0);
            inventoryRecord.setBizType(0);
            inventoryRecord.setBizId(0L);
            inventoryRecord.setBizItemId(0L);
            inventoryRecord.setStatus(1);
            inventoryRecord.setRemark("同步Idosell商品初始化库存");
            inventoryRecord.setWarehouseId(0L);
            inventoryRecord.setPositionId(0L);
            inventoryRecord.setCreateBy("system");
            oGoodsInventoryRecordMapper.insert(inventoryRecord);
        }else{
            log.info("========商品库存存在，开始更新：{}============",oGoodsSku.getId());
            OGoodsInventory update = new OGoodsInventory();
            update.setId(existingInventory.getId());
            update.setQuantity(goodsSkuList.get(0).getStockQuantity()); // 使用IdoSell的库存数量
            update.setGoodsName(oGoodsSku.getGoodsName());
            update.setColorImage(oGoodsSku.getColorImage());
            update.setColorValue(oGoodsSku.getColorValue());
            update.setSizeValue(oGoodsSku.getSizeValue());
            update.setUpdateTime(new Date());
            update.setUpdateBy("IdoSell同步");
            oGoodsInventoryMapper.updateById(update);

            // 增加库存入库记录
            OGoodsInventoryRecord inventoryRecord = new OGoodsInventoryRecord();
            inventoryRecord.setInventoryId(update.getId());
            inventoryRecord.setGoodsId(oGoodsSku.getGoodsId());
            inventoryRecord.setGoodsNum(oGoodsSku.getGoodsNum());
            inventoryRecord.setSkuId(oGoodsSku.getId());
            inventoryRecord.setSkuCode(oGoodsSku.getSkuCode());
            inventoryRecord.setBatchId(0L);
            inventoryRecord.setType(1);
            inventoryRecord.setInventoryDetailId(0L);
            inventoryRecord.setQuantity(update.getQuantity().intValue());
            inventoryRecord.setBalanceQuantity(inventoryRecord.getQuantity());
            inventoryRecord.setLockedQuantity(0);
            inventoryRecord.setBizType(0);
            inventoryRecord.setBizId(0L);
            inventoryRecord.setBizItemId(0L);
            inventoryRecord.setStatus(1);
            inventoryRecord.setRemark("强制同步Idosell商品");
            inventoryRecord.setWarehouseId(0L);
            inventoryRecord.setPositionId(0L);
            inventoryRecord.setCreateBy("system");
            oGoodsInventoryRecordMapper.insert(inventoryRecord);
        }
        return ResultVo.success();
    }
}




