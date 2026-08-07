package cn.qihangerp.open.idosell.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.domain.OShop;
import cn.qihangerp.domain.OShopPlatform;
import cn.qihangerp.module.goods.domain.OGoodsInventory;
import cn.qihangerp.module.goods.domain.OGoodsSku;
import cn.qihangerp.oms.service.OGoodsInventoryService;
import cn.qihangerp.oms.service.OGoodsSkuService;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellGoodsSkuStock;
import cn.qihangerp.module.open.idosell.domain.bo.IdosellGoodsBo;
import cn.qihangerp.module.open.idosell.domain.vo.IdosellGoods;
import cn.qihangerp.module.open.idosell.domain.vo.IdosellGoodsSku;
import cn.qihangerp.open.idosell.helper.IdosellStockApiHelper;
import cn.qihangerp.module.open.idosell.mapper.OmsIdosellGoodsSkuStockMapper;
import cn.qihangerp.open.idosell.service.IIdosellGoodsService;
import cn.qihangerp.open.idosell.service.IIdosellGoodsSkuService;
import cn.qihangerp.open.idosell.service.IdosellStockCommonService;
import cn.qihangerp.oms.service.OShopPlatformService;
import cn.qihangerp.oms.service.OShopService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdosellStockCommonServiceImpl implements IdosellStockCommonService {
    private final IIdosellGoodsSkuService idosellGoodsSkuService;
    private final IIdosellGoodsService idosellGoodsService;
    private final OmsIdosellGoodsSkuStockMapper skuStockMapper;
    private final IdosellStockApiHelper stockApiHelper;
    private final OShopPlatformService shopPlatformService;
    private final OShopService shopService;
    private final OGoodsInventoryService goodsInventoryService;
    private final OGoodsSkuService oGoodsSkuService;

    @Override
    public void pullShopStockList(Long shopId, String appKey, String appSecret, String url) {
        Integer currPage = 1;
        Integer pageSize = 10;
        boolean hasNextPage = true;
        while (hasNextPage) {
            PageQuery pageQuery = new PageQuery();
            pageQuery.setPageNum(currPage);
            pageQuery.setPageSize(pageSize);
            PageResult<IdosellGoods> goodsPageResult = idosellGoodsService.queryPageList(new IdosellGoodsBo(), pageQuery);
            if (goodsPageResult.getRecords().isEmpty()) {
                hasNextPage = false;
                return;
            }
            if(goodsPageResult.getTotal() > currPage * pageSize){
                hasNextPage = true;
                currPage++;
            }

            List<String> productIds = goodsPageResult.getRecords().stream().map(x -> x.getProductId()).collect(Collectors.toList());
            try {
                var response = stockApiHelper.getStockList(url,appKey, productIds);
                // 调用查询接口
                if (response.getCode() == 0) {
                    if(response.getList()!=null && response.getList().size()>0){
                        for(var sku:response.getList()){
                            List<IdosellGoods> goodsList = goodsPageResult.getRecords().stream().filter(x -> x.getProductId().equals(sku.getProductId())).collect(Collectors.toList());
                            IdosellGoods idosellGoods = null;
                            if(goodsList!=null && goodsList.size()>0){
                                idosellGoods = goodsList.get(0);
                            }
                            List<IdosellGoodsSku> skus = idosellGoodsSkuService.list(
                                    new LambdaQueryWrapper<IdosellGoodsSku>()
                                            .eq(IdosellGoodsSku::getProductId, sku.getProductId())
                                            .eq(IdosellGoodsSku::getSizeId,sku.getSizeId())
                            );
                            OmsIdosellGoodsSkuStock skuStock = new OmsIdosellGoodsSkuStock();
                            skuStock.setProductId(sku.getProductId());
                            skuStock.setProductName(idosellGoods!=null?idosellGoods.getProductName():"");
                            skuStock.setGoodsNum(idosellGoods!=null?idosellGoods.getGoodsNum():"");
                            skuStock.setColorImage(idosellGoods!=null?idosellGoods.getMainImage():"");
                            if(!skus.isEmpty()){
                                skuStock.setColorValue(skus.get(0).getColorName());
                            }
                            skuStock.setSizeId(sku.getSizeId());
                            skuStock.setSizeName(sku.getSizeName());
                            skuStock.setQuantity(sku.getQuantity());
                            skuStock.setStockId(sku.getStockId());
                            skuStock.setProductSizeCode(sku.getProductSizeCode());
                            List<OmsIdosellGoodsSkuStock> list = skuStockMapper.selectList(
                                    new LambdaQueryWrapper<OmsIdosellGoodsSkuStock>()
                                            .eq(OmsIdosellGoodsSkuStock::getProductId, skuStock.getProductId())
                                            .eq(OmsIdosellGoodsSkuStock::getSizeId, skuStock.getSizeId())
                                            .eq(OmsIdosellGoodsSkuStock::getStockId, skuStock.getStockId())
                            );
                            if (list.isEmpty()) {
                                skuStock.setShopId(shopId);
                                skuStock.setCreateTime(new Date());
                                skuStockMapper.insert(skuStock);
                                log.info("===新增Idosell库存数据{}-{}", skuStock.getProductId(), skuStock.getSizeId());
                            } else {
                                skuStock.setId(list.get(0).getId());
                                skuStock.setUpdateTime(new Date());
                                skuStockMapper.updateById(skuStock);
                                log.info("====更新Idosell库存数据{}-{}",skuStock.getProductId(), skuStock.getSizeId());
                            }
                        }

                    }

                }
            } catch (IOException e) {
                log.error("Idosell库存处理失败", e);
            }
        }
        log.info("======Idosell库存拉取完成============");
    }

    @Override
    public ResultVo pushShopStock(Long shopId, Long inventoryId) {
        OGoodsInventory oGoodsInventory = goodsInventoryService.getById(inventoryId);
        if(oGoodsInventory==null) {
            log.error("没有找到库存ID:{}",inventoryId);
            return ResultVo.error("没有找到库存ID");
        }
        OShop shop = shopService.getById(shopId);
        if (shop == null) {
            log.info("没有找到Idosell店铺信息");
            return ResultVo.error("没有找到Idosell店铺信息");
        }
        log.info("=======开始同步库存到{}{}", shop.getId(), shop.getName());
        OShopPlatform platform = shopPlatformService.selectById(EnumShopType.IDOSELL.getIndex());
        if (platform == null) {
            log.info("没有找到Idosell平台配置信息");
            return ResultVo.error("没有找到Idosell平台配置信息");
        }

        // 查出对应的oGoodsSku
        OGoodsSku oGoodsSku = oGoodsSkuService.getById(oGoodsInventory.getSkuId());
        if(oGoodsSku==null) {
            log.error("没有找到oGoodsSku:{}",oGoodsInventory.getSkuId());
            return ResultVo.error("没有找到oGoodsSku");
        }

        // 查找库存对应的idosell商品sku(
        List<IdosellGoodsSku> idosellGoodsSkus = idosellGoodsSkuService.list(
                new LambdaQueryWrapper<IdosellGoodsSku>()
                        .eq(IdosellGoodsSku::getOGoodsSkuId, oGoodsSku.getId()));
        if(idosellGoodsSkus==null||idosellGoodsSkus.isEmpty()) {
            log.error("没有找到idosellGoodsSku:{}",oGoodsSku.getId());
            return ResultVo.error("没有找到idosellGoodsSku");
        }
        IdosellGoodsSku sku = idosellGoodsSkus.get(0);
        try {
            var response = stockApiHelper.updateProductStock(platform.getServerUrl(),platform.getAppKey()
                    ,sku.getProductSizeCodeExternal(),sku.getStockId(),oGoodsInventory.getQuantity());
            if(response.getCode()==0){
                return ResultVo.success(response);
            }else {
                return ResultVo.error(response.getMsg());
            }
        }catch (Exception e) {
            log.error("====同步库存到Idosell错误："+e.getMessage());
            return ResultVo.error(e.getMessage());
        }
    }


}
