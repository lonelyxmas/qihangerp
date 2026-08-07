package cn.qihangerp.open.shein.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.domain.OShop;
import cn.qihangerp.domain.OShopPlatform;
import cn.qihangerp.module.goods.domain.*;
import cn.qihangerp.module.goods.mapper.*;
import cn.qihangerp.oms.service.OGoodsAttributeValueService;
import cn.qihangerp.module.open.shein.domain.*;
import cn.qihangerp.open.shein.helper.SheinStockApiHelper;
import cn.qihangerp.module.open.shein.mapper.*;
import cn.qihangerp.open.shein.request.SheinGoodsBo;
import cn.qihangerp.oms.service.OShopPlatformService;
import cn.qihangerp.oms.service.OShopService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.open.shein.service.OmsSheinGoodsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author qilip
* @description 针对表【oms_shein_goods(shein商品表)】的数据库操作Service实现
* @createDate 2025-03-11 15:23:49
*/
@Slf4j
@AllArgsConstructor
@Service
public class OmsSheinGoodsServiceImpl extends ServiceImpl<OmsSheinGoodsMapper, OmsSheinGoods>
    implements OmsSheinGoodsService {
    private final OmsSheinGoodsMapper sheinGoodsMapper;
    private final OmsSheinGoodsSkuMapper sheinGoodsSkuMapper;
    private final OmsSheinGoodsSkuStockMapper sheinGoodsSkuStockMapper;
//    private final SheinStockCommonService sheinStockCommonService;
    private final OShopPlatformService platformService;
    private final OGoodsCategoryMapper goodsCategoryMapper;
    private final OGoodsMapper oGoodsMapper;

    private final OGoodsSkuMapper oGoodsSkuMapper;
    private final OGoodsAttributeValueService goodsAttributeValueService;
    private final OmsSheinShopCategoryMapper sheinShopCategoryMapper;
    private final OmsSheinProductAttrMapper sheinProductAttrMapper;
    private final OmsSheinProductAttrValMapper sheinProductAttrValMapper;
    private final OShopService shopService;
    private final OGoodsInventoryMapper oGoodsInventoryMapper;
    private final OGoodsInventoryRecordMapper oGoodsInventoryRecordMapper;
    private final OGoodsPublishMapper goodsPublishMapper;

    @Override
    public PageResult<OmsSheinGoods> queryPageList(SheinGoodsBo param, PageQuery pageQuery) {


        LambdaQueryWrapper<OmsSheinGoods> queryWrapper = new LambdaQueryWrapper<OmsSheinGoods>()
                .eq(param.getShopId() != null, OmsSheinGoods::getShopId, param.getShopId())
                .eq(param.getCategoryId() != null, OmsSheinGoods::getCategoryId, param.getCategoryId())
                .eq(StringUtils.hasText(param.getSpuName()), OmsSheinGoods::getSpuName, param.getSpuName())
                .eq(StringUtils.hasText(param.getSupplierCode()), OmsSheinGoods::getSupplierCode, param.getSupplierCode())
                .like(StringUtils.hasText(param.getGoodsName()), OmsSheinGoods::getProductName, param.getGoodsName());
        Page<OmsSheinGoods> orderPage = sheinGoodsMapper.selectPage(pageQuery.build(), queryWrapper);
        if (orderPage.getRecords() != null) {
            for (var order : orderPage.getRecords()) {
                order.setSkuList(sheinGoodsSkuMapper.selectList(new LambdaQueryWrapper<OmsSheinGoodsSku>().eq(OmsSheinGoodsSku::getSpuName, order.getSpuName())));
            }
        }
        return PageResult.build(orderPage);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo sync(SheinGoodsBo bo) {
        log.info("开始同步Shein商品, spuName: {}", bo.getSpuName());
        OmsSheinGoods omsSheinGoods = sheinGoodsMapper.selectOne(
                new LambdaQueryWrapper<OmsSheinGoods>()
                        .eq(OmsSheinGoods::getSpuName, bo.getSpuName())
        );
        if (omsSheinGoods == null) {
            log.error("未找到Shein商品数据");
            return ResultVo.error("未找到Shein商品数据");
        }
        OShop shop = shopService.getById(omsSheinGoods.getShopId());
        if (shop == null) {
            log.error("Shein商品店铺数据错误");
            return ResultVo.error("Shein商品店铺数据错误");
        }

        // 检查分类有没有做映射关系
        OmsSheinShopCategory shopCategory = sheinShopCategoryMapper.selectById(omsSheinGoods.getCategoryId());
        if (shopCategory == null) {
            log.error("未找到Shein店铺分类：{}",omsSheinGoods.getCategoryId());
            return ResultVo.error("未找到Shein店铺分类:"+omsSheinGoods.getCategoryId());
        } else if (shopCategory.getOmsCategoryId() == null || shopCategory.getOmsCategoryId() == 0) {
            log.error("未找到Shein店铺分类的映射关系：｛｝",shopCategory.getCategoryName());
            return ResultVo.error("未找到Shein店铺分类的映射关系:"+shopCategory.getCategoryName());
        }

        OGoodsCategory oGoodsCategory = goodsCategoryMapper.selectById(shopCategory.getOmsCategoryId());
        if (oGoodsCategory == null) {
            log.error("未找到对应的商品库分类数据：{}",shopCategory.getCategoryName());
            return ResultVo.error("未找到对应的商品库分类数据:"+shopCategory.getCategoryName());
        }
        // 检查商品是否已经同步过（使用goods_num检测）
        OGoods existingGoods = oGoodsMapper.selectOne(
                new LambdaQueryWrapper<OGoods>()
                        .eq(OGoods::getGoodsNum, omsSheinGoods.getSupplierCode())
        );

        OGoods goods = new OGoods();
        if (existingGoods != null) {
//            log.info("商品已存在，执行更新操作, goodsId: {}", existingGoods.getId());
            goods.setId(existingGoods.getId());
        }else{
            // 第一次同步的商品，处理库存
            List<OmsSheinGoodsSkuStock> omsSheinGoodsSkuStocksSpu = sheinGoodsSkuStockMapper.selectList(
                    new LambdaQueryWrapper<OmsSheinGoodsSkuStock>()
                            .eq(OmsSheinGoodsSkuStock::getSpuName, omsSheinGoods.getSpuName()));
            if(omsSheinGoodsSkuStocksSpu==null||omsSheinGoodsSkuStocksSpu.isEmpty()){
                // 通过接口拉取库存数量
                try {
                    OShopPlatform platform = platformService.selectById(EnumShopType.SHEIN.getIndex());
                    List<String> spuList = new ArrayList<>();
                    spuList.add(omsSheinGoods.getSpuName());
                    pullSpuQuantity(omsSheinGoods.getShopId(),platform.getAppKey(),platform.getAppSecret(),platform.getServerUrl(),spuList);
                    log.info("Shein单个SPU:{}库存拉取成功",omsSheinGoods.getSpuName());
                }catch (Exception e){
                    log.error("Shein单个SPU:{}库存拉取失败：{}",omsSheinGoods.getSpuName(),e.getMessage());
                }
            }

        }

        try {
            log.info("开始处理商品信息======= goodsNum: {}", omsSheinGoods.getSupplierCode());

            // 使用商品编码查出所有sku
            List<OmsSheinGoodsSku> sheinGoodsSkuList = sheinGoodsSkuMapper.selectList(
                    new LambdaQueryWrapper<OmsSheinGoodsSku>()
                            .eq(OmsSheinGoodsSku::getSpuName, omsSheinGoods.getSpuName())
            );
            if (CollectionUtils.isEmpty(sheinGoodsSkuList)) {
                log.error("未找到Shein SKU数据");
                throw new RuntimeException("未找到Shein SKU数据");
            }

            // 1. 创建或更新商品基本信息
            goods.setGoodsNum(omsSheinGoods.getSupplierCode());
            goods.setName(omsSheinGoods.getProductName());
            // 处理图片URL，将JSON格式的detailImages转换为逗号分隔的字符串
            String imageUrls = omsSheinGoods.getDetailImages();
            String mainImage = omsSheinGoods.getProductImage();

            goods.setImage(imageUrls);
            goods.setMainImage(mainImage);
            goods.setUnitName("件"); // 默认单位
            goods.setCategoryId(oGoodsCategory.getId()); // 转换分类ID
            goods.setBarCode(omsSheinGoods.getSpuName()); // 使用商品编号作为条码
            goods.setStatus(1); // 默认启用

            // 设置默认尺寸和重量
            goods.setLength(0.0);
            goods.setHeight(0.0);
            goods.setWidth(0.0);
            goods.setWidth1(0.0);
            goods.setWidth2(0.0);
            goods.setWidth3(0.0);
            goods.setWeight(0.0);
            goods.setDisable(0);
            goods.setSupplierId(1880650970199887873L);

            // 设置价格信息
            goods.setPurPrice(omsSheinGoods.getCostPriceEur()); // 采购价使用批发价
            goods.setWholePrice(omsSheinGoods.getCostPriceEur());
            goods.setRetailPrice(omsSheinGoods.getCostPriceEur());
            goods.setUnitCost(omsSheinGoods.getCostPriceEur()); // 单位成本使用批发价

            // 设置其他默认值
            goods.setLowQty(0);
            goods.setHighQty(0);
            goods.setCreateBy("Shein同步");
            goods.setCreateTime(new Date());

            // 保存商品基本信息
            if (goods.getId() != null) {
                log.info("商品存在，不更新商品主信息, goodsId: {}====会更新SKU和关联关系", goods.getId());
//                oGoodsMapper.updateById(goods);

            } else {
                oGoodsMapper.insert(goods);
                log.info("商品基本信息新增成功, goodsId: {}", goods.getId());
            }

            List<OGoodsPublish> oGoodsPublishes = goodsPublishMapper.selectList(
                    new LambdaQueryWrapper<OGoodsPublish>()
                            .eq(OGoodsPublish::getGoodsId, goods.getId())
                            .eq(OGoodsPublish::getShopId, shop.getId())
            );
            if(oGoodsPublishes==null||oGoodsPublishes.isEmpty()){
                // 添加发布记录
                OGoodsPublish publish = new OGoodsPublish();
                publish.setGoodsId(goods.getId());
                publish.setShopPlatformId(EnumShopType.SHEIN.getIndex());
                publish.setShopPlatform(EnumShopType.SHEIN.getName());
                publish.setShopId(shop.getId());
                publish.setShopName(shop.getName());
                publish.setPublishTime(new Date());
                goodsPublishMapper.insert(publish);
                log.info("添加发布记录, goodsId: {}", goods.getId());
            }

            // 2. 处理SKU信息
            for (OmsSheinGoodsSku sheinSku : sheinGoodsSkuList) {
                //  颜色尺码 属性查找
                OmsSheinProductAttr colorAttr = sheinProductAttrMapper.selectById(sheinSku.getColorId());
                if (colorAttr == null) {
                    log.error("未找到Shein店铺color属性:{}",sheinSku.getColorId());
                    throw new RuntimeException("未找到Shein店铺color属性:"+sheinSku.getColorId());
                } else if (colorAttr.getOmsAttributeId() == null || colorAttr.getOmsAttributeId() == 0) {
                    log.error("未找到Shein店铺color属性的映射关系:"+colorAttr.getAttributeName());
                    throw new RuntimeException("未找到Shein店铺color属性的映射关系:"+colorAttr.getAttributeName());
                }
                OmsSheinProductAttr sizeAttr = sheinProductAttrMapper.selectById(sheinSku.getSizeId());
                if (sizeAttr == null) {
                    log.error("未找到Shein店铺size属性:{}",sheinSku.getSizeId());
                    throw new RuntimeException("未找到Shein店铺size属性:"+sheinSku.getSizeId());
                } else if (sizeAttr.getOmsAttributeId() == null || sizeAttr.getOmsAttributeId() == 0) {
                    log.error("未找到Shein店铺size属性的映射关系:{}",sizeAttr.getAttributeName());
                    throw new RuntimeException("未找到Shein店铺size属性的映射关系:"+sizeAttr.getAttributeName());
                }

                List<OmsSheinProductAttrVal> colors = sheinProductAttrValMapper.selectList(
                        new LambdaQueryWrapper<OmsSheinProductAttrVal>()
                                .eq(OmsSheinProductAttrVal::getOmsAttributeValueId,sheinSku.getColorValueId()));
                if (colors == null||colors.isEmpty()) {
                    log.error("未找到 Shein 店铺color属性值:{}",sheinSku.getColorValueId());
                    throw new RuntimeException("未找到 Shein 店铺color属性值:"+sheinSku.getColorValueId());
                } else if (colors.get(0).getOmsAttributeValueId() == null || colors.get(0).getOmsAttributeValueId() == 0) {
                    log.error("未找到 Shein 店铺colorId的映射关系===={}", colors.get(0).getAttributeValue());
                    throw new RuntimeException("未找到 Shein 店铺colorId的映射关系"+ colors.get(0).getAttributeValue());
                }
                OGoodsAttributeValue omsColor = goodsAttributeValueService.getById(colors.get(0).getOmsAttributeValueId());
                if (omsColor == null) {
                    log.error("未找到oms color数据:{}",colors.get(0).getAttributeValue());
                    throw new RuntimeException("未找到oms color数据:"+colors.get(0).getAttributeValue());
                }

                List<OmsSheinProductAttrVal> sizes = sheinProductAttrValMapper.selectList(
                        new LambdaQueryWrapper<OmsSheinProductAttrVal>()
                                .eq(OmsSheinProductAttrVal::getOmsAttributeValueId,sheinSku.getSizeValueId()));
                if (sizes == null||sizes.isEmpty()) {
                    log.error("未找到 Shein 店铺size属性值:{}",sizes.get(0).getAttributeValue());
                    throw new RuntimeException("未找到 Shein 店铺size属性值:"+sizes.get(0).getAttributeValue());
                } else if (sizes.get(0).getOmsAttributeValueId() == null || sizes.get(0).getOmsAttributeValueId() == 0) {
                    log.error("未找到 Shein 店铺sizeId的映射关系===={}", sizes.get(0).getAttributeValue());
                    throw new RuntimeException("未找到 Shein 店铺sizeId的映射关系:"+sizes.get(0).getAttributeValue());
                }

                OGoodsAttributeValue omsSize = goodsAttributeValueService.getById(sizes.get(0).getOmsAttributeValueId());
                if (omsSize == null) {
                    log.error("未找到oms size 数据:{}",sizes.get(0).getAttributeValue());
                    throw new RuntimeException("未找到oms size 数据:"+sizes.get(0).getAttributeValue());
                }


                // 检查SKU是否已经同步过(使用goodsNum+colorId+sizeId)
                OGoodsSku existingSku = oGoodsSkuMapper.selectOne(
                        new LambdaQueryWrapper<OGoodsSku>()
                                .eq(OGoodsSku::getGoodsId, goods.getId())
                                .eq(OGoodsSku::getColorValueId, omsColor.getAttributeValueId())
                                .eq(OGoodsSku::getSizeValueId, omsSize.getAttributeValueId())
                );

                OGoodsSku spec = new OGoodsSku();
                if (existingSku != null) {
                    log.info("SKU已存在，执行更新操作, skuId: {}", existingSku.getId());
                    spec.setId(existingSku.getId());
                }

                spec.setGoodsId(goods.getId());
                spec.setGoodsNum(goods.getGoodsNum());
//                spec.setOuterErpGoodsId(idosellGoods.getProductId());
//                spec.setOuterErpSkuId(idosellSku.getSkuId());
                spec.setGoodsName(goods.getName());

                // 构建SKU名称
                StringBuilder skuName = new StringBuilder();
                if (StringUtils.hasText(sheinSku.getColorValue())) {
                    skuName.append(sheinSku.getColorValue());
                }
                if (StringUtils.hasText(sheinSku.getSizeValue())) {
                    if (skuName.length() > 0) {
                        skuName.append(" ");
                    }
                    skuName.append(sheinSku.getSizeValue());
                }
                spec.setSkuName(skuName.toString());
                spec.setSkuCode(sheinSku.getSupplierSku());
                spec.setColorAttributeId(colorAttr.getOmsAttributeId());
                spec.setColorAttribute(colorAttr.getAttributeName());
                spec.setColorValueId(omsColor.getAttributeValueId());
                spec.setColorValue(omsColor.getAttributeValue());
                spec.setSizeAttributeId(sizeAttr.getOmsAttributeId());
                spec.setSizeAttribute(sizeAttr.getAttributeName());
                spec.setSizeValueId(omsSize.getAttributeValueId());
                spec.setSizeValue(omsSize.getAttributeValue());
                // 设置图片
                spec.setColorImage(sheinSku.getColorImage());

                // 设置价格
//                BigDecimal skuWholesalePrice = CurrencyConverterUtils.plnToEur(idosellSku.getWholesalePrice(), rate);
                spec.setPurPrice(sheinSku.getCostPriceEur());
                spec.setRetailPrice(sheinSku.getCostPriceEur());
                spec.setWholePrice(sheinSku.getCostPriceEur());
                spec.setUnitCost(sheinSku.getCostPriceEur());
                spec.setStatus(1);

                // 保存SKU信息
                if (spec.getId() != null) {
                    oGoodsSkuMapper.updateById(spec);
                    log.info("SKU信息更新成功, skuId: {}", spec.getId());
                } else {
                    oGoodsSkuMapper.insert(spec);
                    log.info("SKU信息新增成功, skuId: {}", spec.getId());
                }
                // 更新同步状态
                OmsSheinGoodsSku sheinGoodsSkuUpdata = new OmsSheinGoodsSku();
                sheinGoodsSkuUpdata.setId(sheinSku.getId());
                sheinGoodsSkuUpdata.setSyncStatus((byte) 1); // 已同步
                sheinGoodsSkuUpdata.setUpdateTime(LocalDateTime.now());
                sheinGoodsSkuUpdata.setOGoodsId(goods.getId());
                sheinGoodsSkuUpdata.setOGoodsSkuId(spec.getId());
                sheinGoodsSkuMapper.updateById(sheinGoodsSkuUpdata);

                // 3. 创建或更新库存记录(Shein不更新库存)
                OGoodsInventory existingInventory = oGoodsInventoryMapper.selectOne(
                        new LambdaQueryWrapper<OGoodsInventory>()
                                .eq(OGoodsInventory::getSkuId, spec.getId())
                );
                if(existingInventory==null){
                    log.info("========商品库存不存在，初始化库存数据：{}============",spec.getId());
                    // 查询Shein库存
                     Integer quantity = 0;
                    List<OmsSheinGoodsSkuStock> omsSheinGoodsSkuStocks = sheinGoodsSkuStockMapper.selectList(
                            new LambdaQueryWrapper<OmsSheinGoodsSkuStock>()
                                    .eq(OmsSheinGoodsSkuStock::getSkuCode, sheinSku.getSkuCode()));
                    if(omsSheinGoodsSkuStocks!=null&&omsSheinGoodsSkuStocks.size()>0){
                        quantity = omsSheinGoodsSkuStocks.stream().mapToInt(OmsSheinGoodsSkuStock::getTotalInventoryQuantity).sum();
                    }

                    // 初始化库存
                    OGoodsInventory inventory = new OGoodsInventory();
                    inventory.setSkuId(spec.getId());
                    inventory.setGoodsId(goods.getId());
                    inventory.setGoodsNum(goods.getGoodsNum());
                    inventory.setGoodsName(spec.getGoodsName());
                    inventory.setColorImage(spec.getColorImage());
                    inventory.setColorValue(spec.getColorValue());
                    inventory.setSizeValue(spec.getSizeValue());
                    inventory.setSkuCode(spec.getSkuCode());
                    inventory.setQuantity(quantity);
                    inventory.setIsDelete(0);
                    inventory.setCreateTime(new Date());
                    inventory.setCreateBy("Shein同步");
                    oGoodsInventoryMapper.insert(inventory);
                    log.info("库存信息新增成功, skuId: {}", spec.getId());

                    // 增加库存入库记录
                    OGoodsInventoryRecord inventoryRecord = new OGoodsInventoryRecord();
                    inventoryRecord.setInventoryId(inventory.getId());
                    inventoryRecord.setGoodsId(goods.getId());
                    inventoryRecord.setGoodsNum(goods.getGoodsNum());
                    inventoryRecord.setSkuId(spec.getId());
                    inventoryRecord.setSkuCode(spec.getSkuCode());
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
                }

            }

            // 4. 更新同步状态
            OmsSheinGoods update = new OmsSheinGoods();
            update.setId(omsSheinGoods.getId());
            update.setSyncStatus((byte) 1); // 已同步
            update.setOGoodsId(goods.getId());
            update.setUpdateTime(LocalDateTime.now());
            sheinGoodsMapper.updateById(update);

            log.info("===========推送Shein商品完成========");
            return ResultVo.success();
        } catch (Exception e) {
            log.error("同步Shein商品失败, spuName: {}, error: {}", bo.getSpuName(), e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            throw new RuntimeException("同步失败: " + e.getMessage());
            return ResultVo.error("同步Shein商品失败, spuName:"+bo.getSpuName()+"，error:"+e.getMessage());
        }
    }

    @Override
    public void batchSync() {
        log.info("开始执行批量同步Shein商品...");
        // 设置分页参数
        int pageSize = 10; // 每页处理10条记录
        int pageNum = 1;
        boolean hasMore = true;

        while (hasMore) {
            log.info("开始处理第 {} 页数据", pageNum);
            // 构建分页查询条件
            Page<OmsSheinGoods> page = new Page<>(pageNum, pageSize);
            LambdaQueryWrapper<OmsSheinGoods> queryWrapper = new LambdaQueryWrapper<OmsSheinGoods>()
                    .eq(OmsSheinGoods::getOGoodsId, 0) // 只查询未同步的商品
                    .orderByAsc(OmsSheinGoods::getCreateTime); // 按创建时间升序

            // 执行分页查询
            Page<OmsSheinGoods> goodsPage = sheinGoodsMapper.selectPage(page, queryWrapper);
            List<OmsSheinGoods> records = goodsPage.getRecords();

            if (CollectionUtils.isEmpty(records)) {
                log.info("没有更多需要同步的商品数据");
                hasMore = false;
                break;
            }// 遍历当前页的商品进行同步
            for (OmsSheinGoods goods : records) {
                try {
                    log.info("开始同步商品, spuName: {}", goods.getSpuName());

                    // 构建同步参数
                    SheinGoodsBo bo = new SheinGoodsBo();
                    bo.setSpuName(goods.getSpuName());

                    // 调用同步方法
                    this.sync(bo);

                    log.info("商品同步成功, spuName: {}", goods.getSpuName());

                    // 添加间隔，避免请求过快
                    //Thread.sleep(1000);

                } catch (Exception e) {
                    log.error("同步商品失败, spuName: {}, error: {}", goods.getSpuName(), e.getMessage());
                    // 更新同步状态为失败
                    goods.setSyncStatus((byte) 2); // 2表示同步失败
                    goods.setUpdateTime(LocalDateTime.now());
                    sheinGoodsMapper.updateById(goods);
                }
            }// 判断是否还有下一页
            hasMore = records.size() >= pageSize;
            pageNum++;

            log.info("第 {} 页数据处理完成", pageNum - 1);
        }

        log.info("批量同步任务完成");

    }

    private void pullSpuQuantity(Long shopId,String appKey, String appSecret, String url, List<String> spuNameList) {
        try {
            // 调用查询接口
            var response = SheinStockApiHelper.queryStockList(url, appKey, appSecret, spuNameList);
            if (response.getCode() == 0) {
                if (response.getInfo() != null && !response.getInfo().isEmpty()) {
                    for (var infoData : response.getInfo()) {
                        for (var goods : infoData.getGoodsInventory()) {
                            for (var sku : goods.getSkuList()) {
                                List<OmsSheinGoodsSku> skus = sheinGoodsSkuMapper.selectList(
                                        new LambdaQueryWrapper<OmsSheinGoodsSku>()
                                                .eq(OmsSheinGoodsSku::getSkuCode, sku.getSkuCode()));

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

                                List<OmsSheinGoodsSkuStock> list = sheinGoodsSkuStockMapper.selectList(
                                        new LambdaQueryWrapper<OmsSheinGoodsSkuStock>()
                                                .eq(OmsSheinGoodsSkuStock::getSkuCode, sku.getSkuCode()));
                                if (list.isEmpty()) {
                                    skuStock.setShopId(shopId);
                                    skuStock.setCreateTime(new Date());
                                    sheinGoodsSkuStockMapper.insert(skuStock);
                                    log.info("===新增Shein库存数据{}", skuStock.getSkuCode());
                                } else {
                                    skuStock.setId(list.get(0).getId());
                                    skuStock.setUpdateTime(new Date());
                                    sheinGoodsSkuStockMapper.updateById(skuStock);
                                    log.info("====更新Shein库存数据{}", skuStock.getSkuCode());
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("查询商品列表失败", e);
        }
    }
}




