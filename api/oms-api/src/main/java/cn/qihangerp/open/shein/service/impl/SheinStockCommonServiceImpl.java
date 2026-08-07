package cn.qihangerp.open.shein.service.impl;

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
import cn.qihangerp.module.open.shein.domain.OmsSheinGoods;
import cn.qihangerp.module.open.shein.domain.OmsSheinGoodsSku;
import cn.qihangerp.module.open.shein.domain.OmsSheinGoodsSkuStock;
import cn.qihangerp.open.shein.helper.SheinStockApiHelper;
import cn.qihangerp.open.shein.helper.model.*;
import cn.qihangerp.open.shein.helper.model.UpdateGoodsInventoryRequest;
import cn.qihangerp.open.shein.request.SheinGoodsBo;
import cn.qihangerp.open.shein.service.*;
import cn.qihangerp.oms.service.OShopPlatformService;
import cn.qihangerp.oms.service.OShopService;
import cn.qihangerp.open.shein.service.OmsSheinGoodsService;
import cn.qihangerp.open.shein.service.OmsSheinGoodsSkuService;
import cn.qihangerp.open.shein.service.OmsSheinGoodsSkuStockService;
import cn.qihangerp.open.shein.service.SheinStockCommonService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SheinStockCommonServiceImpl implements SheinStockCommonService {
    private final OmsSheinGoodsSkuService sheinGoodsSkuService;
    private final OmsSheinGoodsService sheinGoodsService;
    private final OmsSheinGoodsSkuStockService skuStockService;
    private final OGoodsInventoryService goodsInventoryService;
    private final OGoodsSkuService oGoodsSkuService;
    private final OShopPlatformService shopPlatformService;
    private final OShopService shopService;

    @Override
    public void pullShopStockList(Long shopId, String appKey, String appSecret, String url) {

        Integer currPage = 1;
        Integer pageSize = 10;
        boolean hasNextPage = true;

        while (hasNextPage) {
            PageQuery pageQuery = new PageQuery();
            pageQuery.setPageNum(currPage);
            pageQuery.setPageSize(pageSize);
            PageResult<OmsSheinGoods> goodsPageResult = sheinGoodsService.queryPageList(new SheinGoodsBo(), pageQuery);
            if (goodsPageResult.getRecords().isEmpty()) {
                hasNextPage = false;
                return;
            }
            if(goodsPageResult.getTotal() > currPage * pageSize){
                hasNextPage = true;
                currPage++;
            }

            List<String> spuList = goodsPageResult.getRecords().stream().map(x -> x.getSpuName()).collect(Collectors.toList());
            try {
                // 
                var response = SheinStockApiHelper.queryStockList(url, appKey, appSecret, spuList);
                if (response.getCode() == 0) {
                    if (response.getInfo() != null && !response.getInfo().isEmpty()) {
                        for (var infoData : response.getInfo()) {
                            for (var goods : infoData.getGoodsInventory()) {
                                for (var sku : goods.getSkuList()) {
                                    List<OmsSheinGoodsSku> skus = sheinGoodsSkuService.list(new LambdaQueryWrapper<OmsSheinGoodsSku>().eq(OmsSheinGoodsSku::getSkuCode, sku.getSkuCode()));

                                    OmsSheinGoodsSkuStock skuStock = new OmsSheinGoodsSkuStock();
                                    if(!skus.isEmpty()){
                                        skuStock.setProductName(skus.get(0).getProductName());
                                        skuStock.setSupplierCode(skus.get(0).getSupplierCode());
                                        skuStock.setSupplierSku(skus.get(0).getSkuCode());
                                        skuStock.setColorImage(skus.get(0).getColorImage());
                                        skuStock.setColorValue(skus.get(0).getColorValue());
                                        skuStock.setSizeValue(skus.get(0).getSizeValue());
                                    }
                                    skuStock.setSpuName(goods.getSpuName());
                                    skuStock.setSkcName(goods.getSkcName());
                                    skuStock.setSkuCode(sku.getSkuCode());
                                    skuStock.setTotalInventoryQuantity(sku.getTotalInventoryQuantity());
                                    skuStock.setTotalLockedQuantity(sku.getTotalLockedQuantity());
                                    skuStock.setTotalOutOfStockQty(sku.getTotalOutOfStockQty());
                                    skuStock.setTotalTempLockQuantity(sku.getTotalTempLockQuantity());
                                    skuStock.setTotalUsableInventory(sku.getTotalUsableInventory());
                                    if (!sku.getWarehouseInventoryList().isEmpty()) {
                                        skuStock.setInventoryQuantity(sku.getWarehouseInventoryList().get(0).getInventoryQuantity());
                                        skuStock.setLockedQuantity(sku.getWarehouseInventoryList().get(0).getLockedQuantity());
                                        skuStock.setOutOfStockQty(sku.getWarehouseInventoryList().get(0).getOutOfStockQty());
                                        skuStock.setTempLockQuantity(sku.getWarehouseInventoryList().get(0).getTempLockQuantity());
                                        skuStock.setUsableInventory(sku.getWarehouseInventoryList().get(0).getUsableInventory());
                                        skuStock.setWarehouseCode(sku.getWarehouseInventoryList().get(0).getWarehouseCode());
                                        skuStock.setWarehouseType(sku.getWarehouseInventoryList().get(0).getWarehouseType());
                                    }

                                    List<OmsSheinGoodsSkuStock> list = skuStockService.list(new LambdaQueryWrapper<OmsSheinGoodsSkuStock>().eq(OmsSheinGoodsSkuStock::getSkuCode, sku.getSkuCode()));
                                    if (list.isEmpty()) {
                                        skuStock.setShopId(shopId);
                                        skuStock.setCreateTime(new Date());
                                        skuStockService.save(skuStock);
                                        log.info("===Shein{}", skuStock.getSkuCode());
                                    } else {
                                        skuStock.setId(list.get(0).getId());
                                        skuStock.setUpdateTime(new Date());
                                        skuStockService.updateById(skuStock);
                                        log.info("====Shein{}", skuStock.getSkuCode());
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                log.error("", e);
            }
            log.info("======Shein============");
        }

    }

    @Override
    public void pullSpuQuantity(Long shopId,String appKey, String appSecret, String url, List<String> spuNameList) {
        try {
            // 
            var response = SheinStockApiHelper.queryStockList(url, appKey, appSecret, spuNameList);
            if (response.getCode() == 0) {
                if (response.getInfo() != null && !response.getInfo().isEmpty()) {
                    for (var infoData : response.getInfo()) {
                        for (var goods : infoData.getGoodsInventory()) {
                            for (var sku : goods.getSkuList()) {
                                List<OmsSheinGoodsSku> skus = sheinGoodsSkuService.list(new LambdaQueryWrapper<OmsSheinGoodsSku>().eq(OmsSheinGoodsSku::getSkuCode, sku.getSkuCode()));

                                OmsSheinGoodsSkuStock skuStock = new OmsSheinGoodsSkuStock();
                                if(!skus.isEmpty()){
                                    skuStock.setProductName(skus.get(0).getProductName());
                                    skuStock.setSupplierCode(skus.get(0).getSupplierCode());
                                    skuStock.setSupplierSku(skus.get(0).getSkuCode());
                                    skuStock.setColorImage(skus.get(0).getColorImage());
                                    skuStock.setColorValue(skus.get(0).getColorValue());
                                    skuStock.setSizeValue(skus.get(0).getSizeValue());
                                }
                                skuStock.setSpuName(goods.getSpuName());
                                skuStock.setSkcName(goods.getSkcName());
                                skuStock.setSkuCode(sku.getSkuCode());
                                skuStock.setTotalInventoryQuantity(sku.getTotalInventoryQuantity());
                                skuStock.setTotalLockedQuantity(sku.getTotalLockedQuantity());
                                skuStock.setTotalOutOfStockQty(sku.getTotalOutOfStockQty());
                                skuStock.setTotalTempLockQuantity(sku.getTotalTempLockQuantity());
                                skuStock.setTotalUsableInventory(sku.getTotalUsableInventory());
                                if (!sku.getWarehouseInventoryList().isEmpty()) {
                                    skuStock.setInventoryQuantity(sku.getWarehouseInventoryList().get(0).getInventoryQuantity());
                                    skuStock.setLockedQuantity(sku.getWarehouseInventoryList().get(0).getLockedQuantity());
                                    skuStock.setOutOfStockQty(sku.getWarehouseInventoryList().get(0).getOutOfStockQty());
                                    skuStock.setTempLockQuantity(sku.getWarehouseInventoryList().get(0).getTempLockQuantity());
                                    skuStock.setUsableInventory(sku.getWarehouseInventoryList().get(0).getUsableInventory());
                                    skuStock.setWarehouseCode(sku.getWarehouseInventoryList().get(0).getWarehouseCode());
                                    skuStock.setWarehouseType(sku.getWarehouseInventoryList().get(0).getWarehouseType());
                                }

                                List<OmsSheinGoodsSkuStock> list = skuStockService.list(new LambdaQueryWrapper<OmsSheinGoodsSkuStock>().eq(OmsSheinGoodsSkuStock::getSkuCode, sku.getSkuCode()));
                                if (list.isEmpty()) {
                                    skuStock.setShopId(shopId);
                                    skuStock.setCreateTime(new Date());
                                    skuStockService.save(skuStock);
                                    log.info("===Shein{}", skuStock.getSkuCode());
                                } else {
                                    skuStock.setId(list.get(0).getId());
                                    skuStock.setUpdateTime(new Date());
                                    skuStockService.updateById(skuStock);
                                    log.info("====Shein{}", skuStock.getSkuCode());
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("", e);
        }
    }

    @Override
    public ResultVo pushShopStock(Long shopId, Long inventoryId) {
        OGoodsInventory oGoodsInventory = goodsInventoryService.getById(inventoryId);
        if(oGoodsInventory==null) {
            log.error("ID:{}",inventoryId);
            return ResultVo.error("ID");
        }
        OShop shop = shopService.getById(shopId);
        if (shop == null) {
            log.info("Shein");
            return ResultVo.error("Shein");
        }
        log.info("======={}{}", shop.getId(), shop.getName());
        OShopPlatform platform = shopPlatformService.selectById(EnumShopType.SHEIN.getIndex());
        if (platform == null) {
            log.info("Shein");
            return ResultVo.error("Shein");
        }

        // oGoodsSku
        OGoodsSku oGoodsSku = oGoodsSkuService.getById(oGoodsInventory.getSkuId());
        if(oGoodsSku==null) {
            log.error("oGoodsSku:{}",oGoodsInventory.getSkuId());
            return ResultVo.error("oGoodsSku");
        }

        // Sheinsku
        List<OmsSheinGoodsSku> sheinGoodsSkuList = sheinGoodsSkuService.list(
                new LambdaQueryWrapper<OmsSheinGoodsSku>()
                        .eq(OmsSheinGoodsSku::getOGoodsSkuId, oGoodsSku.getId()));
        if(sheinGoodsSkuList==null||sheinGoodsSkuList.isEmpty()) {
            log.error("SheinGoodsSku:{}",oGoodsSku.getId());
            return ResultVo.error("SheinGoodsSku");
        }
        OmsSheinGoodsSku sku = sheinGoodsSkuList.get(0);
        try {
            UpdateGoodsInventoryRequest request = new UpdateGoodsInventoryRequest();
            List<UpdateGoodsInventoryRequest.UpdateGoodsInventoryHolder> stockList = new ArrayList<>();
            UpdateGoodsInventoryRequest.UpdateGoodsInventoryHolder stock=new UpdateGoodsInventoryRequest.UpdateGoodsInventoryHolder();
            stock.setSkc(sku.getSkcName());
            stock.setSkuCode(sku.getSkuCode());
            stock.setAvailableNumber(oGoodsInventory.getQuantity().toString());
            stockList.add(stock);
            request.setStock(stockList);
            request.setUrl(platform.getServerUrl());
            request.setAppKey(platform.getAppKey());
            request.setAppSecret(platform.getAppSecret());

            var response = SheinStockApiHelper.syncGoodsInventory(request);
            if(response.getCode()==0){
                return ResultVo.success(response);
            }else {
                return ResultVo.error(response.getMsg());
            }
        }catch (Exception e) {
            log.error("====Idosell error: " + e.getMessage());
            return ResultVo.error(e.getMessage());
        }
    }
}
