package cn.qihangerp.open.shein.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.module.goods.domain.OGoodsInventory;
import cn.qihangerp.module.goods.domain.OGoodsInventoryRecord;
import cn.qihangerp.module.goods.domain.OGoodsSku;
import cn.qihangerp.module.goods.mapper.OGoodsInventoryMapper;
import cn.qihangerp.module.goods.mapper.OGoodsInventoryRecordMapper;
import cn.qihangerp.oms.service.OGoodsSkuService;
import cn.qihangerp.module.open.shein.domain.OmsSheinGoodsSku;
import cn.qihangerp.module.open.shein.domain.bo.SheinGoodsStockBo;
import cn.qihangerp.module.open.shein.mapper.OmsSheinGoodsSkuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.open.shein.domain.OmsSheinGoodsSkuStock;
import cn.qihangerp.open.shein.service.OmsSheinGoodsSkuStockService;
import cn.qihangerp.module.open.shein.mapper.OmsSheinGoodsSkuStockMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
* @author qilip
* @description 针对表【oms_shein_goods_sku_stock(库存信息表)】的数据库操作Service实现
* @createDate 2025-03-10 18:48:40
*/
@Slf4j
@AllArgsConstructor
@Service
public class OmsSheinGoodsSkuStockServiceImpl extends ServiceImpl<OmsSheinGoodsSkuStockMapper, OmsSheinGoodsSkuStock>
    implements OmsSheinGoodsSkuStockService{
    private final OmsSheinGoodsSkuStockMapper skuStockMapper;
    private final OGoodsInventoryMapper oGoodsInventoryMapper;
    private final OGoodsInventoryRecordMapper oGoodsInventoryRecordMapper;
    private final OGoodsSkuService oGoodsSkuService;
    private final OmsSheinGoodsSkuMapper omsSheinGoodsSkuMapper;
    @Override
    public PageResult<OmsSheinGoodsSkuStock> queryPageList(SheinGoodsStockBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OmsSheinGoodsSkuStock> queryWrapper = new LambdaQueryWrapper<OmsSheinGoodsSkuStock>();
        queryWrapper.eq(StringUtils.hasText(bo.getGoodsNum()), OmsSheinGoodsSkuStock::getSupplierCode, bo.getGoodsNum());
        queryWrapper.eq(StringUtils.hasText(bo.getSkuCode()), OmsSheinGoodsSkuStock::getSupplierSku, bo.getSkuCode());
        queryWrapper.eq(StringUtils.hasText(bo.getSpuName()), OmsSheinGoodsSkuStock::getSpuName, bo.getSpuName());
        queryWrapper.eq(StringUtils.hasText(bo.getSkcName()), OmsSheinGoodsSkuStock::getSkcName, bo.getSkcName());
        queryWrapper.eq(StringUtils.hasText(bo.getSkuId()),OmsSheinGoodsSkuStock::getSkuCode, bo.getSkuId());

        Page<OmsSheinGoodsSkuStock> sheinGoodsSkuPage = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(sheinGoodsSkuPage);
    }

    @Transactional
    @Override
    public ResultVo pushStockToOms(Long id) {
        OmsSheinGoodsSkuStock skuStock = this.baseMapper.selectById(id);
        if(skuStock==null){
            log.error("没有找到库存信息");
            return ResultVo.error("Data Error");
        }

        // 查找商品数据 productId+sizeId
        List<OmsSheinGoodsSku> goodsSkuList = omsSheinGoodsSkuMapper.selectList(
                new LambdaQueryWrapper<OmsSheinGoodsSku>()
                        .eq(OmsSheinGoodsSku::getSkuCode, skuStock.getSkuCode())
        );
        if(goodsSkuList==null||goodsSkuList.size()==0){
            log.error("没有找到Shein Goods Sku：{}",skuStock.getSkuCode());
            return ResultVo.error("Data Error：Not Found Goods Sku");
        } else if (goodsSkuList.get(0).getOGoodsSkuId()==null||goodsSkuList.get(0).getOGoodsSkuId()==0) {
            log.error("Shein Goods Sku没有关联OMS商品SKU：{}",skuStock.getSkuCode());
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
            inventory.setQuantity(skuStock.getTotalInventoryQuantity()); // 使用Shein的库存数量
            inventory.setIsDelete(0);
            inventory.setCreateTime(new Date());
            inventory.setCreateBy("Shein同步");
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
            inventoryRecord.setRemark("同步Shein商品初始化库存");
            inventoryRecord.setWarehouseId(0L);
            inventoryRecord.setPositionId(0L);
            inventoryRecord.setCreateBy("system");
            oGoodsInventoryRecordMapper.insert(inventoryRecord);
        }else{
            log.info("========商品库存存在，开始更新：{}============",oGoodsSku.getId());
            OGoodsInventory update = new OGoodsInventory();
            update.setId(existingInventory.getId());
            update.setQuantity(skuStock.getTotalInventoryQuantity()); // 使用Shein的库存数量
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
            inventoryRecord.setRemark("强制同步Shein商品");
            inventoryRecord.setWarehouseId(0L);
            inventoryRecord.setPositionId(0L);
            inventoryRecord.setCreateBy("system");
            oGoodsInventoryRecordMapper.insert(inventoryRecord);
        }
        return ResultVo.success();
    }
}




