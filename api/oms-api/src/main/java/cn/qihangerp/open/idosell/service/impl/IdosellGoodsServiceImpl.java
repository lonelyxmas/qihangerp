package cn.qihangerp.open.idosell.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.enums.EnumShopType;

import cn.qihangerp.domain.OShop;
import cn.qihangerp.domain.OShopPlatform;
import cn.qihangerp.module.goods.domain.*;
import cn.qihangerp.module.goods.mapper.*;
import cn.qihangerp.oms.service.OGoodsAttributeValueService;
import cn.qihangerp.oms.service.OShopCategoryAttributeValueRelationService;
import cn.qihangerp.oms.service.OGoodsCategoryRelationService;

import cn.qihangerp.module.open.idosell.domain.OmsIdosellSaleAttr;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellSaleAttrVal;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellShopCategory;
import cn.qihangerp.module.open.idosell.domain.bo.IdosellGoodsBo;
import cn.qihangerp.module.open.idosell.domain.vo.IdosellGoods;
import cn.qihangerp.module.open.idosell.domain.vo.IdosellGoodsSku;
import cn.qihangerp.open.idosell.helper.IdosellApiHelper;
import cn.qihangerp.module.open.idosell.mapper.IdosellGoodsMapper;
import cn.qihangerp.module.open.idosell.mapper.IdosellGoodsSkuMapper;
import cn.qihangerp.module.open.idosell.mapper.OmsIdosellShopCategoryMapper;
import cn.qihangerp.open.idosell.model.ProductResponse;
import cn.qihangerp.open.idosell.service.IIdosellGoodsService;
import cn.qihangerp.open.idosell.service.OmsIdosellSaleAttrService;
import cn.qihangerp.open.idosell.service.OmsIdosellSaleAttrValService;
import cn.qihangerp.open.idosell.utils.CurrencyConverterUtils;
import cn.qihangerp.open.idosell.utils.PartNumberUtils;
import cn.qihangerp.oms.service.OShopPlatformService;
import cn.qihangerp.oms.service.OShopService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * IdoSell商品信息表 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2025-02-08
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IdosellGoodsServiceImpl extends ServiceImpl<IdosellGoodsMapper, IdosellGoods> implements IIdosellGoodsService {
    private final IdosellGoodsMapper mapper;
    private final IdosellGoodsSkuMapper skuMapper;
    private final IdosellApiHelper idosellApiHelper;
    private final OShopPlatformService platformService;
    private final OGoodsMapper oGoodsMapper;
    private final OGoodsSkuMapper oGoodsSkuMapper;
    private final OGoodsCategoryMapper goodsCategoryMapper;
    private final OGoodsInventoryMapper oGoodsInventoryMapper;
    private final OGoodsInventoryRecordMapper oGoodsInventoryRecordMapper;
    private final OGoodsAttributeValueService goodsAttributeValueService;
    private final OGoodsCategoryRelationService shopCategoryRelationService;
    private final OShopCategoryAttributeValueRelationService shopCategoryAttributeValueRelationService;
    private final OmsIdosellSaleAttrService saleAttrService;
    private final OmsIdosellSaleAttrValService saleAttrValService;
    private final OmsIdosellShopCategoryMapper shopCategoryMapper;
    private final OShopService shopService;
    private final OGoodsPublishMapper goodsPublishMapper;
    @Override
    public PageResult<IdosellGoods> queryPageList(IdosellGoodsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<IdosellGoods> queryWrapper = new LambdaQueryWrapper<IdosellGoods>()
                .eq(bo.getShopId() != null, IdosellGoods::getShopId, bo.getShopId())
                .eq(bo.getCategoryId() != null, IdosellGoods::getCategoryId, bo.getCategoryId())
                .like(StringUtils.hasText(bo.getGoodsNum()), IdosellGoods::getGoodsNum, bo.getGoodsNum());

        Page<IdosellGoods> goodsPage = mapper.selectPage(pageQuery.build(), queryWrapper);
//        List<IdosellGoods> records = goodsPage.getRecords();
//        List<String> productIdList = records.stream().map(IdosellGoods::getProductId).toList();
//        Map<String, List<IdosellGoodsSku>> skuListGroupByProductId = skuMapper.selectList(new LambdaQueryWrapper<IdosellGoodsSku>().in(IdosellGoodsSku::getProductId, productIdList)).stream().collect(Collectors.groupingBy(IdosellGoodsSku::getProductId, Collectors.toList()));
//        records = records.stream().peek(record-> record.setIdosellGoodsSkuList(skuListGroupByProductId.get(record.getProductId()))).toList();
//        goodsPage.setRecords(records);
        if(goodsPage.getRecords()!=null){
            for (var goods:goodsPage.getRecords()) {
                goods.setSkuList(skuMapper.selectList(new LambdaQueryWrapper<IdosellGoodsSku>().eq(IdosellGoodsSku::getProductId,goods.getProductId())));
            }
        }
        return PageResult.build(goodsPage);
    }

    @Override
    public void
    importGoods(Long shopId) {
        OShopPlatform platform = platformService.selectById(EnumShopType.IDOSELL.getIndex());
        ObjectMapper objectMapper = new ObjectMapper();
        log.info("开始导入IdoSell商品...");
        final int PAGE_SIZE = 50; // 增加每页数量以提高效率
        int currentPage = 0;
        boolean hasMorePages = true;

        // 分页循环获取数据
        while (hasMorePages) {
            log.info(" 开始读取 第 {} 页数据：", currentPage);
            try {
                // 构建查询参数
                IdosellApiHelper.ProductListParams params = new IdosellApiHelper.ProductListParams.Builder()
                        .page(currentPage)
                        .limit(PAGE_SIZE)
//                                .categoryIds(collect)
                        .sortById(false)
                        .build();

                String response = idosellApiHelper.getProductList(platform.getServerUrl(), platform.getAppKey(), params);

                // 解析响应数据
                ProductResponse productResponse = objectMapper.readValue(response, ProductResponse.class);
                log.info("  - 总记录数: {}", productResponse.getResultsNumberAll());
                log.info("  - 当前页商品数: {}", productResponse.getResults().size());
                log.info("  - 总页数: {}", productResponse.getResultsNumberPage());
                if (productResponse.getResults() == null || productResponse.getResults().isEmpty()) {
                    log.info(" 第 {} 页未获取到商品数据", currentPage);
                    break;
                }

                // 处理当前页的商品数据
                List<ProductResponse.Product> results = productResponse.getResults();
                for (ProductResponse.Product source : results) {
                    try {
                        if (source.getProductStocksData() != null && source.getProductStocksData().getProductStocksQuantities() != null && source.getProductStocksData().getProductStocksQuantities().size() > 0) {
                            String s = JSONObject.toJSONString(source);
                            IdosellGoods target = this.convertToIdoSellGoods(source);
                            Long oGoodsId = 0L;
                            // 查询商品库商品信息
                            List<OGoods> oGoodsList = oGoodsMapper.selectList(new LambdaQueryWrapper<OGoods>()
                                    .eq(OGoods::getGoodsNum, target.getGoodsNum()));
                            if (!oGoodsList.isEmpty()) {
                                oGoodsId = oGoodsList.get(0).getId();
                            } else {
                                log.error("==========没有找到OGoods信息========");
                            }
                            target.setOGoodsId(oGoodsId);
                            IdosellGoods idosellGoods = mapper.selectOne(
                                    new LambdaQueryWrapper<>(IdosellGoods.class)
                                            .eq(IdosellGoods::getProductId, target.getProductId())
                            );
                            target.setShopId(shopId);
                            if (idosellGoods == null) {
                                mapper.insert(target);
                                log.info("新增商品成功, productId: {}", target.getProductId());
                            } else {
                                target.setId(idosellGoods.getId()); // 设置ID以便更新
                                mapper.updateById(target);
                                log.info("更新商品成功, productId: {}", target.getProductId());
                            }

                            List<IdosellGoodsSku> idosellGoodsSkus = this.convertToIdoSellGoodsSku(source, oGoodsId);
                            for (IdosellGoodsSku goodsSku : idosellGoodsSkus) {
                                goodsSku.setOGoodsId(oGoodsId);
                                IdosellGoodsSku existingSku = skuMapper.selectOne(
                                        new LambdaQueryWrapper<>(IdosellGoodsSku.class)
                                                .eq(IdosellGoodsSku::getSkuId, goodsSku.getSkuId())
                                );

                                if (existingSku == null) {
                                    skuMapper.insert(goodsSku);
                                    log.info("新增SKU成功, skuId: {}", goodsSku.getSkuId());
                                } else {
                                    goodsSku.setId(existingSku.getId()); // 设置ID以便更新
                                    skuMapper.updateById(goodsSku);
                                    log.info("更新SKU成功, skuId: {}", goodsSku.getSkuId());
                                }
                            }
                        } else {
                            log.error("========idosell没有库存数据，不添加{}=========", source.getProductId());
                        }
                    } catch (Exception e) {
                        log.error("处理商品 ID: {} 时发生错误: {}", source.getProductId(), e.getMessage());
                        // 继续处理下一个商品
                    }
                }

                // 判断是否还有下一页
                hasMorePages = shouldContinueToNextPage(productResponse, currentPage);
                currentPage++;

                // 添加请求间隔，避免请求过快
                Thread.sleep(1000);

            } catch (Exception e) {
                log.error("处理第 {} 页数据时发生错误: {}", currentPage, e.getMessage());
                // 记录错误但继续处理下一个分类
            }
        }
        log.info("导入IdoSell商品完成=========");

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVo sync(IdosellGoodsBo bo) {
        log.info("开始同步IdoSell商品, productId: {}", bo.getProductId());
        // 获取IdoSell商品数据
        Integer productId = bo.getProductId();
        IdosellGoods idosellGoods = mapper.selectOne(
                new LambdaQueryWrapper<IdosellGoods>()
                        .eq(IdosellGoods::getProductId, productId)
        );
        if (idosellGoods == null) {
            log.error("未找到IdoSell商品数据");
            return ResultVo.error("未找到IdoSell商品数据");

        }
        OShop shop = shopService.getById(idosellGoods.getShopId());
        if(shop==null){
            log.error("IdoSell商品店铺数据错误");
            return ResultVo.error("IdoSell商品店铺数据错误");
        }
        // 检查分类有没有做映射关系
        OmsIdosellShopCategory omsIdosellShopCategory = shopCategoryMapper.selectById(idosellGoods.getCategoryId());
        if(omsIdosellShopCategory==null) {
            log.error("未找到IdoSell店铺分类");
            return ResultVo.error("未找到IdoSell店铺分类");
        } else if (omsIdosellShopCategory.getOmsCategoryId()==null||omsIdosellShopCategory.getOmsCategoryId()==0) {
            log.error("未找到IdoSell店铺分类的映射关系:{}",omsIdosellShopCategory.getName());
            return ResultVo.error("未找到IdoSell店铺分类的映射关系:"+omsIdosellShopCategory.getName());
        }

        OGoodsCategory oGoodsCategory = goodsCategoryMapper.selectById(omsIdosellShopCategory.getOmsCategoryId());
        if(oGoodsCategory==null) {
            log.error("未找到对应的商品库分类数据：{}",omsIdosellShopCategory.getName());
            return ResultVo.error("未找到对应的商品库分类数据："+omsIdosellShopCategory.getName());
        }
        // 检查商品是否已经同步过（使用goods_num检测）
        OGoods existingGoods = oGoodsMapper.selectOne(
                new LambdaQueryWrapper<OGoods>()
                        .eq(OGoods::getGoodsNum, idosellGoods.getGoodsNum())
        );
        OGoods goods = new OGoods();
        if (existingGoods != null) {
            log.info("商品已存在，执行更新操作, goodsId: {}", existingGoods.getId());
            goods.setId(existingGoods.getId());
        }

        try {
            log.info("开始处理商品信息======= goodsNum: {}", idosellGoods.getGoodsNum());

            // 使用商品编码查出所有sku
            List<IdosellGoodsSku> idosellGoodsSkus = skuMapper.selectList(
                new LambdaQueryWrapper<IdosellGoodsSku>()
                    .eq(IdosellGoodsSku::getGoodsNum, idosellGoods.getGoodsNum())
            );
            if (CollectionUtils.isEmpty(idosellGoodsSkus)) {
                throw new RuntimeException("未找到IdoSell SKU数据");
            }

            // 1. 创建或更新商品基本信息
            goods.setGoodsNum(idosellGoods.getGoodsNum());
            goods.setName(idosellGoods.getProductName());
            // 处理图片URL，将JSON格式的detailImages转换为逗号分隔的字符串
            String imageUrls = idosellGoods.getMainImage();
            String mainImage = idosellGoods.getMainImage();
            try {
                if (StringUtils.hasText(idosellGoods.getDetailImages())) {
                    List<String> detailImageList = JSON.parseArray(idosellGoods.getDetailImages(), String.class);
                    if (!detailImageList.isEmpty()) {
                        String detailUrls = detailImageList.stream()
                            .filter(StringUtils::hasText)
                            .distinct()
                            .collect(Collectors.joining(","));
                        if (!detailImageList.contains(idosellGoods.getMainImage())) {
                            imageUrls = imageUrls + "," + detailUrls;
                        } else {
                            imageUrls = detailUrls;
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("解析商品详情图片失败，使用主图, productId: {}, error: {}", idosellGoods.getProductId(), e.getMessage());
            }
            goods.setImage(imageUrls);
            goods.setMainImage(mainImage);
//            goods.setOuterErpGoodsId(idosellGoods.getProductId());
            goods.setUnitName("件"); // 默认单位
//            goods.setCategoryId(convertToErpCategoryId(idosellGoods.getCategoryId())); // 转换分类ID
            goods.setCategoryId(oGoodsCategory.getId()); // 转换分类ID
            goods.setBarCode(idosellGoods.getGoodsNum()); // 使用商品编号作为条码
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
            BigDecimal rate = BigDecimal.valueOf(4.1719);
            BigDecimal wholesalePrice = CurrencyConverterUtils.plnToEur(idosellGoods.getWholesalePrice(), rate);
            goods.setPurPrice(wholesalePrice); // 采购价使用批发价
            goods.setWholePrice(wholesalePrice);
            BigDecimal retailPrice = CurrencyConverterUtils.plnToEur(idosellGoods.getRetailPrice(), rate);
            goods.setRetailPrice(retailPrice);
            goods.setUnitCost(wholesalePrice); // 单位成本使用批发价
            
            // 设置其他默认值
            goods.setLowQty(0);
            goods.setHighQty(0);
            goods.setCreateBy("IdoSell同步");
            goods.setCreateTime(new Date());
            
            // 保存商品基本信息
            if (goods.getId() != null) {
//                oGoodsMapper.updateById(goods);
                log.info("商品存在，不更新商品主信息, goodsId: {}====会更新SKU和关联关系", goods.getId());
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
                publish.setShopPlatformId(EnumShopType.IDOSELL.getIndex());
                publish.setShopPlatform(EnumShopType.IDOSELL.getName());
                publish.setShopId(shop.getId());
                publish.setShopName(shop.getName());
                publish.setPublishTime(new Date());
                goodsPublishMapper.insert(publish);
            }

            // 2. 处理SKU信息
            for (IdosellGoodsSku idosellSku : idosellGoodsSkus) {
                // idosell 颜色尺码值是固定的
                OmsIdosellSaleAttr idosellColorAttr = saleAttrService.getById(100);
                if(idosellColorAttr==null) {
                    log.error("未找到IdoSell店铺color属性");
                    throw new RuntimeException("未找到IdoSell店铺color属性");
                } else if (idosellColorAttr.getOmsAttributeId()==null||idosellColorAttr.getOmsAttributeId()==0) {
                    log.error("未找到IdoSell店铺color属性的映射关系");
                    throw new RuntimeException("未找到IdoSell店铺color属性的映射关系");
                }
                OmsIdosellSaleAttr idosellSizeAttr = saleAttrService.getById(200);
                if(idosellSizeAttr==null) {
                    log.error("未找到IdoSell店铺size属性");
                    throw new RuntimeException("未找到IdoSell店铺size属性");
                } else if (idosellSizeAttr.getOmsAttributeId()==null||idosellSizeAttr.getOmsAttributeId()==0) {
                    log.error("未找到IdoSell店铺size属性的映射关系");
                    throw new RuntimeException("未找到IdoSell店铺size属性的映射关系");
                }

                OmsIdosellSaleAttrVal color = saleAttrValService.getById(idosellSku.getColorId());
                if(color==null) {
                    log.error("未找到IdoSell店铺color属性值:{}",idosellSku.getColorName());
                    throw new RuntimeException("未找到IdoSell店铺color属性值:"+idosellSku.getColorName());
                } else if (color.getOmsAttributeValueId()==null||color.getOmsAttributeValueId()==0) {
                    log.error("未找到IdoSell店铺colorId的映射关系===={}",color.getAttributeValue());
                    throw new RuntimeException("未找到IdoSell店铺colorId的映射关系===="+color.getAttributeValue());
                }
                OGoodsAttributeValue omsColor = goodsAttributeValueService.getById(color.getOmsAttributeValueId());
                if(omsColor==null) {
                    log.error("未找到oms color数据：{}",color.getOmsAttributeValueId());
                    throw new RuntimeException("未找到oms color数据:"+color.getOmsAttributeValueId());
                }

                OmsIdosellSaleAttrVal size = saleAttrValService.getById(idosellSku.getSizeValueId());
                if(size==null) {
                    log.error("未找到IdoSell店铺size属性值:{}",idosellSku.getSizeName());
                    throw new RuntimeException("未找到IdoSell店铺size属性值:"+idosellSku.getSizeName());
                } else if (size.getOmsAttributeValueId()==null||size.getOmsAttributeValueId()==0) {
                    log.error("未找到IdoSell店铺sizeId的映射关系===={}",color.getAttributeValue());
                    throw new RuntimeException("未找到IdoSell店铺sizeId的映射关系====="+color.getAttributeValue());
                }

                OGoodsAttributeValue omsSize = goodsAttributeValueService.getById(size.getOmsAttributeValueId());
                if(omsSize==null) {
                    log.error("未找到oms size 数据:{}",size.getOmsAttributeValueId());
                    throw new RuntimeException("未找到oms size 数据:"+size.getOmsAttributeValueId());
                }


                // 检查SKU是否已经同步过(使用goodsNum+colorId+sizeId)
                OGoodsSku existingSku = oGoodsSkuMapper.selectOne(
                    new LambdaQueryWrapper<OGoodsSku>()
                        .eq(OGoodsSku::getGoodsNum, idosellSku.getGoodsNum())
                            .eq(OGoodsSku::getColorValueId,omsColor.getAttributeValueId())
                            .eq(OGoodsSku::getSizeValueId,omsSize.getAttributeValueId())
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
                spec.setGoodsName(idosellGoods.getProductName());
                
                // 构建SKU名称
                StringBuilder skuName = new StringBuilder();
                if (StringUtils.hasText(idosellSku.getColor())) {
                    skuName.append(idosellSku.getColor());
                }
                if (StringUtils.hasText(idosellSku.getSizeName())) {
                    if (skuName.length() > 0) {
                        skuName.append(" ");
                    }
                    skuName.append(idosellSku.getSizeName());
                }
                spec.setSkuName(skuName.toString());
                spec.setColorAttributeId(idosellColorAttr.getOmsAttributeId());
                spec.setColorAttribute(idosellColorAttr.getAttributeName());
                spec.setColorValueId(omsColor.getAttributeValueId());
                spec.setColorValue(omsColor.getAttributeValue());
                spec.setSizeAttributeId(idosellSizeAttr.getOmsAttributeId());
                spec.setSizeAttribute(idosellSizeAttr.getAttributeName());
                spec.setSizeValueId(omsSize.getAttributeValueId());
                spec.setSizeValue(omsSize.getAttributeValue());
                // 设置SKU编码
//                spec.setSkuCode(idosellSku.getSkuId());
                // 设置图片
                spec.setColorImage(idosellGoods.getMainImage());
                // 设置价格
                BigDecimal skuWholesalePrice = CurrencyConverterUtils.plnToEur(idosellSku.getWholesalePrice(), rate);
                BigDecimal skuRetailPrice = CurrencyConverterUtils.plnToEur(idosellSku.getRetailPrice(), rate);
                spec.setPurPrice(skuWholesalePrice);
                spec.setRetailPrice(skuRetailPrice);
                spec.setWholePrice(skuWholesalePrice);
                spec.setUnitCost(skuWholesalePrice);
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
                IdosellGoodsSku idosellGoodsSkuUpdata = new IdosellGoodsSku();
                idosellGoodsSkuUpdata.setId(idosellSku.getId());
                idosellGoodsSkuUpdata.setSyncStatus((byte) 1); // 已同步
                idosellGoodsSkuUpdata.setUpdateTime(LocalDateTime.now());
                idosellGoodsSkuUpdata.setOGoodsId(goods.getId());
                idosellGoodsSkuUpdata.setOGoodsSkuId(spec.getId());
                skuMapper.updateById(idosellGoodsSkuUpdata);

                // 3. 创建或更新库存记录

                OGoodsInventory existingInventory = oGoodsInventoryMapper.selectOne(
                    new LambdaQueryWrapper<OGoodsInventory>()
                        .eq(OGoodsInventory::getSkuId, spec.getId())
                );
                if(existingInventory==null){
                    log.info("========商品库存不存在，初始化库存数据：{}============",spec.getId());
                    // 初始化库存
                    OGoodsInventory inventory = new OGoodsInventory();
                    inventory.setSkuId(spec.getId());
                    inventory.setGoodsId(goods.getId());
                    inventory.setGoodsNum(idosellGoods.getGoodsNum());
//                inventory.setSkuCode(idosellSku.getsku());
                    inventory.setGoodsName(spec.getGoodsName());
                    inventory.setColorImage(spec.getColorImage());
                    inventory.setColorValue(spec.getColorValue());
                    inventory.setSizeValue(spec.getSizeValue());
                    inventory.setQuantity(idosellSku.getStockQuantity()); // 使用IdoSell的库存数量
                    inventory.setIsDelete(0);
                    inventory.setCreateTime(new Date());
                    inventory.setCreateBy("IdoSell同步");
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
                    inventoryRecord.setRemark("同步Idosell商品初始化库存");
                    inventoryRecord.setWarehouseId(0L);
                    inventoryRecord.setPositionId(0L);
                    inventoryRecord.setCreateBy("system");
                    oGoodsInventoryRecordMapper.insert(inventoryRecord);
                }
            }
            
            // 4. 更新同步状态
            List<IdosellGoods> idosellGoodsList = mapper.selectList(new LambdaQueryWrapper<IdosellGoods>().eq(IdosellGoods::getGoodsNum, idosellGoods.getGoodsNum()));
            if(idosellGoodsList!=null || !idosellGoodsList.isEmpty()) {
                for(var ig:idosellGoodsList){
                    IdosellGoods update = new IdosellGoods();
                    update.setId(ig.getId());
                    update.setSyncStatus((byte) 1); // 已同步
                    update.setOGoodsId(goods.getId());
                    update.setUpdateTime(LocalDateTime.now());
                    mapper.updateById(update);
                }
            }

            log.info("同步状态更新成功");
            return ResultVo.success();
        } catch (Exception e) {
            log.error("同步IdoSell商品失败, productId: {}, error: {}", bo.getProductId(), e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//            throw new RuntimeException("同步失败: " + e.getMessage());
            return ResultVo.error("同步IdoSell商品失败, productId: "+ bo.getProductId()+", error: "+e.getMessage());
        }
    }

    @Override
    public void batchSync() {
        log.info("开始执行批量同步IdoSell商品...");
//        try {
            // 设置分页参数
            int pageSize = 10; // 每页处理10条记录
            int pageNum = 1;
            boolean hasMore = true;

            while (hasMore) {
                log.info("开始处理第 {} 页数据", pageNum);

                // 构建分页查询条件
                Page<IdosellGoods> page = new Page<>(pageNum, pageSize);
                LambdaQueryWrapper<IdosellGoods> queryWrapper = new LambdaQueryWrapper<IdosellGoods>()
                        .eq(IdosellGoods::getOGoodsId, 0) // 只查询未同步的商品
                        .orderByAsc(IdosellGoods::getCreateTime); // 按创建时间升序

                // 执行分页查询
                Page<IdosellGoods> goodsPage = mapper.selectPage(page, queryWrapper);
                List<IdosellGoods> records = goodsPage.getRecords();

                if (CollectionUtils.isEmpty(records)) {
                    log.info("没有更多需要同步的商品数据");
                    hasMore = false;
                    break;
                }// 遍历当前页的商品进行同步
                for (IdosellGoods goods : records) {
                    try {
                        log.info("开始同步商品, productId: {}", goods.getProductId());

                        // 构建同步参数
                        IdosellGoodsBo bo = new IdosellGoodsBo();
                        bo.setProductId(Integer.valueOf(goods.getProductId()));

                        // 调用同步方法
                        this.sync(bo);

                        log.info("商品同步成功, productId: {}", goods.getProductId());

                        // 添加间隔，避免请求过快
                        //Thread.sleep(1000);

                    } catch (Exception e) {
                        log.error("同步商品失败, productId: {}, error: {}", goods.getProductId(), e.getMessage());
                        // 更新同步状态为失败
                        goods.setSyncStatus((byte) 2); // 2表示同步失败
                        goods.setUpdateTime(LocalDateTime.now());
                        mapper.updateById(goods);
                    }
                }// 判断是否还有下一页
                hasMore = records.size() >= pageSize;
                pageNum++;

                log.info("第 {} 页数据处理完成", pageNum - 1);
            }

            log.info("批量同步任务完成");

//        } catch (Exception e) {
//            log.error("批量同步任务执行失败: {}", e.getMessage(), e);
//            throw new RuntimeException("批量同步任务执行失败", e);
//        }
    }

    /**
     * 判断是否需要继续获取下一页
     */
    private boolean shouldContinueToNextPage(ProductResponse productResponse, int currentPage) {
        if (productResponse == null || productResponse.getResultsNumberPage() == null) {
            return false;
        }

        // 当前页码从0开始，所以需要加1进行比较
        return (currentPage + 1) < productResponse.getResultsNumberPage();
    }

    /**
     * 将IdoSell商品数据转换为本地存储格式
     */
    private IdosellGoods convertToIdoSellGoods(ProductResponse.Product product) {
        IdosellGoods goods = new IdosellGoods();

        try {
            goods.setProductId(String.valueOf(product.getProductId()));
            goods.setProductIsDeleted(product.getProductIsDeleted());
            goods.setProductIsVisible(product.getProductIsVisible());
            goods.setDelivererId(product.getDelivererId());
            goods.setDelivererName(product.getDelivererName());
            goods.setCurrencyId(product.getCurrencyId());
            goods.setProductVat(product.getProductVat());
            goods.setProductVatFree(product.getProductVatFree());
            goods.setProductAddingTime(product.getProductAddingTime());
            goods.setProductQuantityChangedTime(product.getProductQuantityChangedTime());
            // 获取商品名称（优先使用英文）
            String productName = product.getProductDescriptionsLangData().stream()
                    .filter(desc -> "pol".equals(desc.getLangId()))
                    .map(ProductResponse.ProductDescription::getProductName)
                    .findFirst()
                    .orElseGet(() -> product.getProductDescriptionsLangData().get(0).getProductName());
            goods.setProductName(productName);

            // 获取商品描述
            String productDesc = product.getProductDescriptionsLangData().stream()
                    .filter(desc -> "pol".equals(desc.getLangId()))
                    .map(ProductResponse.ProductDescription::getProductDescription)
                    .findFirst()
                    .orElseGet(() -> product.getProductDescriptionsLangData().get(0).getProductDescription());
            goods.setProductDesc(productDesc);

            goods.setCategoryId(product.getCategoryId());
            goods.setCategoryName(product.getCategoryName());
            goods.setProductDisplayedCode(product.getProductDisplayedCode());
            String goodsNum = PartNumberUtils.extractPartNumber(product.getProductDisplayedCode());
            goods.setGoodsNum(goodsNum);
            goods.setRetailPrice(BigDecimal.valueOf(product.getProductRetailPrice()));
            goods.setWholesalePrice(BigDecimal.valueOf(product.getProductWholesalePrice()));
            // 处理图片
            if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
                // 设置主图（优先级最高的图片）
                ProductResponse.ProductImage mainImage = product.getProductImages().stream()
                        .min(Comparator.comparing(ProductResponse.ProductImage::getProductImagePriority))
                        .orElse(product.getProductImages().get(0));
                goods.setMainImage(mainImage.getProductImageLargeUrl());

                // 设置详情图（JSON数组格式）
                List<String> detailImages = product.getProductImages().stream()
                        .map(ProductResponse.ProductImage::getProductImageLargeUrl)
                        .collect(Collectors.toList());
                goods.setDetailImages(JSON.toJSONString(detailImages));
            }
            // 初始状态：未同步
            goods.setSyncStatus((byte) 0);
            LocalDateTime now = LocalDateTime.now();
            goods.setCreateTime(now);
            goods.setUpdateTime(now);

        } catch (Exception e) {
            log.error("转换商品数据失败, productId: {}", product.getProductId(), e);
            throw new RuntimeException("转换商品数据失败", e);
        }

        return goods;
    }

    /**
     * 将IdoSell商品SKU数据转换为本地存储格式
     */
    private List<IdosellGoodsSku> convertToIdoSellGoodsSku(ProductResponse.Product product,Long oGoodsId) {
        List<IdosellGoodsSku> skuList = new ArrayList<>();

        try {
            if (product.getProductStocksData() == null ||
                    product.getProductStocksData().getProductStocksQuantities() == null) {
                return skuList;
            }

            // 遍历每个仓库的库存数据
            for (ProductResponse.ProductStockQuantity stockQuantity :
                    product.getProductStocksData().getProductStocksQuantities()) {
                Integer stockId = stockQuantity.getStockId();
                // 遍历每个尺码的数据
                for (ProductResponse.ProductSizeData sizeData : stockQuantity.getProductSizesData()) {
                    IdosellGoodsSku sku = new IdosellGoodsSku();
                    String goodsNum = PartNumberUtils.extractPartNumber(product.getProductDisplayedCode());
                    sku.setGoodsNum(goodsNum);
                    sku.setStockId(stockId);
                    sku.setProductSizeCodeExternal(sizeData.getProductSizeCodeExternal());
                    // 设置基本信息
                    sku.setProductId(String.valueOf(product.getProductId()));
                    String uniqueSkuId = generateUniqueSkuId(product.getProductId(), sizeData.getSizeId());
                    sku.setSkuId(uniqueSkuId);

                    // 设置尺码信息
                    sku.setSizeId(sizeData.getSizeId());
                    sku.setSizeName(sizeData.getSizeId());
                    sku.setSizePanelName(sizeData.getSizePanelName());


                    Long omsColorValueId = 0L;
                    Long omsSizeValueId = 0L;

                    // 添加Size到数据库
                    List<OmsIdosellSaleAttrVal> sizeValList = saleAttrValService.list(
                            new LambdaQueryWrapper<OmsIdosellSaleAttrVal>()
                                    .eq(OmsIdosellSaleAttrVal::getAttributeValue, sku.getSizeId())
                                    .eq(OmsIdosellSaleAttrVal::getAttributeId,200)
                    );

                    if(sizeValList == null || sizeValList.isEmpty()) {
                        OmsIdosellSaleAttrVal attrVal = new OmsIdosellSaleAttrVal();
                        attrVal.setAttributeId(200L);//属性id是固定的
                        attrVal.setAttributeValue(sku.getSizeId());
                        attrVal.setSizePanelName(sku.getSizePanelName());
                        saleAttrValService.save(attrVal);
                        sku.setSizeValueId(attrVal.getAttributeValueId().toString());
                        log.info("新增size属性值{}",JSONObject.toJSONString(attrVal));
                    }else{
                        log.info("===size属性值已存在，{} ====关联的OMS {}",sizeValList.get(0).getAttributeValueId(),sizeValList.get(0).getOmsAttributeValueId());
                        sku.setSizeValueId(sizeValList.get(0).getAttributeValueId().toString());
                        if(sizeValList.get(0).getOmsAttributeValueId()==null||sizeValList.get(0).getOmsAttributeValueId()==0){
                            log.error("==========idosell sizeValueId没有关联oms属性值id========");
                        }else{
                            omsSizeValueId = sizeValList.get(0).getOmsAttributeValueId();
                        }
                    }

                    // 使用增强版颜色提取
                    String color = extractEnhancedColorInfo(product, sizeData);
                    if (color != null) {
                        sku.setColor(color);
                        sku.setColorName(color);
//                        sku.setColorId(color);
                        log.info("成功提取颜色信息 - 商品ID: {}, SKU: {}, 颜色: {}", 
                            product.getProductId(), uniqueSkuId, color);
                        // 添加颜色到数据库
                        List<OmsIdosellSaleAttrVal> colorValList = saleAttrValService.list(
                                new LambdaQueryWrapper<OmsIdosellSaleAttrVal>()
                                        .eq(OmsIdosellSaleAttrVal::getAttributeValue, color)
                                        .eq(OmsIdosellSaleAttrVal::getAttributeId,100)
                        );
                        if(colorValList == null || colorValList.isEmpty()) {
                            OmsIdosellSaleAttrVal attrVal = new OmsIdosellSaleAttrVal();
                            attrVal.setAttributeId(100L);//属性id是固定的
                            attrVal.setAttributeValue(color);
                            saleAttrValService.save(attrVal);
                            sku.setColorId(attrVal.getAttributeValueId().toString());
                            log.info("新增颜色属性值{}",JSONObject.toJSONString(attrVal));
                        }else{
                            log.info("===color属性值已存在，{} ====关联的OMS {}",sizeValList.get(0).getAttributeValueId(),sizeValList.get(0).getOmsAttributeValueId());
                            sku.setColorId(colorValList.get(0).getAttributeValueId().toString());
                            if(colorValList.get(0).getOmsAttributeValueId()==null||colorValList.get(0).getOmsAttributeValueId()==0){
                                log.error("==========idosell colorValueId没有关联oms属性值id========");
                            }else{
                                omsColorValueId = colorValList.get(0).getOmsAttributeValueId();
                            }
                        }

                    } else {
                        log.warn("未能提取到颜色信息 - 商品ID: {}, SKU: {}", 
                            product.getProductId(), uniqueSkuId);
                    }
                    // 查询商品库goodssku
                    Long oGoodsSkuId=0L;
                    // oGoodsId+omsColorValueId+omsSizeValueId
                    List<OGoodsSku> oGoodsSkuList = oGoodsSkuMapper.selectList(
                            new LambdaQueryWrapper<OGoodsSku>()
                                    .eq(OGoodsSku::getGoodsId, oGoodsId)
                                    .eq(OGoodsSku::getColorValueId, omsColorValueId)
                                    .eq(OGoodsSku::getSizeValueId, omsSizeValueId)
                    );
                    if(oGoodsSkuList.isEmpty()){
                        log.error("===============没有找到oGoodsSku数据");
                    }else{
                        oGoodsSkuId = oGoodsSkuList.get(0).getId();
                    }
                    sku.setOGoodsSkuId(oGoodsSkuId);
                    // 设置库存
                    int availableStock = calculateAvailableStock(sizeData);
                    sku.setStockQuantity(availableStock);

                    // 设置价格（使用商品级别的价格）
                    sku.setRetailPrice(BigDecimal.valueOf(product.getProductRetailPrice()));
                    sku.setWholesalePrice(BigDecimal.valueOf(product.getProductWholesalePrice()));

                    LocalDateTime now = LocalDateTime.now();
                    sku.setCreateTime(now);
                    sku.setUpdateTime(now);

                    skuList.add(sku);
                }
            }

        } catch (Exception e) {
            log.error("转换SKU数据失败, productId: {}", product.getProductId(), e);
            throw new RuntimeException("转换SKU数据失败", e);
        }

        return skuList;
    }

    /**
     * 增强版颜色信息提取
     */
    private String extractEnhancedColorInfo(ProductResponse.Product product, ProductResponse.ProductSizeData sizeData) {
        // 1. 首先尝试从参数中获取颜色（原有方法）
        String colorFromParams = extractColorInfo(product);
        if (StringUtils.hasText(colorFromParams)) {
            return colorFromParams;
        }

        // 2. 从商品名称中提取颜色
        String colorFromName = extractColorFromName(product);
        if (StringUtils.hasText(colorFromName)) {
            return colorFromName;
        }

        // 3. 从SKU编码中提取颜色
        String colorFromSku = extractColorFromSku(sizeData);
        if (StringUtils.hasText(colorFromSku)) {
            return colorFromSku;
        }

        // 4. 从商品描述中提取颜色
        String colorFromDesc = extractColorFromDescription(product);
        if (StringUtils.hasText(colorFromDesc)) {
            return colorFromDesc;
        }

        // 如果都没有找到，返回默认值或null
        return null;
    }

    /**
     * 从商品名称中提取颜色
     */
    private String extractColorFromName(ProductResponse.Product product) {
        if (product.getProductDescriptionsLangData() == null) {
            return null;
        }

        // 常见颜色关键词映射
        Map<String, String> colorKeywords = new HashMap<>();
        colorKeywords.put("black", "Black");
        colorKeywords.put("white", "White");
        colorKeywords.put("red", "Red");
        colorKeywords.put("blue", "Blue");
        colorKeywords.put("green", "Green");
        colorKeywords.put("yellow", "Yellow");
        colorKeywords.put("pink", "Pink");
        colorKeywords.put("purple", "Purple");
        colorKeywords.put("grey", "Grey");
        colorKeywords.put("gray", "Grey");
        colorKeywords.put("brown", "Brown");
        colorKeywords.put("navy", "Navy");
        colorKeywords.put("orange", "Orange");
        // 添加波兰语颜色关键词
        colorKeywords.put("czarny", "Black");
        colorKeywords.put("biały", "White");
        colorKeywords.put("czerwony", "Red");
        colorKeywords.put("niebieski", "Blue");
        colorKeywords.put("zielony", "Green");
        colorKeywords.put("żółty", "Yellow");
        colorKeywords.put("różowy", "Pink");
        colorKeywords.put("fioletowy", "Purple");
        colorKeywords.put("szary", "Grey");
        colorKeywords.put("brązowy", "Brown");
        colorKeywords.put("granatowy", "Navy");
        colorKeywords.put("pomarańczowy", "Orange");

        // 获取商品名称（优先英文）
        String productName = product.getProductDescriptionsLangData().stream()
                .filter(desc -> "eng".equals(desc.getLangId()))
                .map(ProductResponse.ProductDescription::getProductName)
                .findFirst()
                .orElseGet(() -> product.getProductDescriptionsLangData().get(0).getProductName());

        if (productName == null) {
            return null;
        }

        // 转换为小写并分词
        String[] words = productName.toLowerCase().split("\\s+");
        for (String word : words) {
            if (colorKeywords.containsKey(word)) {
                return colorKeywords.get(word);
            }
        }

        return null;
    }

    /**
     * 从SKU编码中提取颜色信息
     */
    private String extractColorFromSku(ProductResponse.ProductSizeData sizeData) {
        if (sizeData == null || sizeData.getProductSizeCodeExternal() == null) {
            return null;
        }

        // 假设SKU编码格式为：xxx-COLOR-SIZE 或 COLOR-SIZE-xxx
        String skuCode = sizeData.getProductSizeCodeExternal();
        
        // 常见颜色代码映射
        Map<String, String> colorCodes = new HashMap<>();
        colorCodes.put("BK", "Black");
        colorCodes.put("WH", "White");
        colorCodes.put("RD", "Red");
        colorCodes.put("BL", "Blue");
        colorCodes.put("GR", "Green");
        colorCodes.put("YL", "Yellow");
        colorCodes.put("PK", "Pink");
        colorCodes.put("PP", "Purple");
        colorCodes.put("GY", "Grey");
        colorCodes.put("BR", "Brown");
        colorCodes.put("NV", "Navy");
        colorCodes.put("OR", "Orange");

        // 分割SKU编码
        String[] parts = skuCode.split("-|_");
        for (String part : parts) {
            if (colorCodes.containsKey(part.toUpperCase())) {
                return colorCodes.get(part.toUpperCase());
            }
        }

        return null;
    }

    /**
     * 从商品描述中提取颜色
     */
    private String extractColorFromDescription(ProductResponse.Product product) {
        if (product.getProductDescriptionsLangData() == null) {
            return null;
        }

        // 获取商品描述（优先英文）
        String description = product.getProductDescriptionsLangData().stream()
                .filter(desc -> "eng".equals(desc.getLangId()))
                .map(ProductResponse.ProductDescription::getProductDescription)
                .findFirst()
                .orElseGet(() -> product.getProductDescriptionsLangData().get(0).getProductDescription());

        if (description == null) {
            return null;
        }

        // 颜色关键词正则表达式
        Pattern colorPattern = Pattern.compile("(?i)color[:\\s]+(\\w+)|colou?r[:\\s]+(\\w+)|kolor[:\\s]+(\\w+)");
        Matcher matcher = colorPattern.matcher(description);
        
        if (matcher.find()) {
            String color = null;
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (matcher.group(i) != null) {
                    color = matcher.group(i);
                    break;
                }
            }
            if (color != null) {
                // 首字母大写
                return color.substring(0, 1).toUpperCase() + color.substring(1).toLowerCase();
            }
        }

        return null;
    }

    /**
     * 从商品参数中提取颜色
     */
    private String extractColorInfo(ProductResponse.Product product) {
        if (product.getProductParameters() == null) {
            return null;
        }

        // 遍历所有参数，查找颜色参数
        for (ProductResponse.ProductParameter parameter : product.getProductParameters()) {
            // 检查参数描述中是否包含颜色相关的关键词
            boolean isColorParameter = parameter.getParameterDescriptionsLangData().stream()
                    .anyMatch(desc -> {
                        String paramName = desc.getParameterName().toLowerCase();
                        return paramName.contains("color") || paramName.contains("colour") ||
                                paramName.contains("kolor"); // 波兰语的color
                    });

            if (isColorParameter && parameter.getParameterValues() != null &&
                    !parameter.getParameterValues().isEmpty()) {
                // 获取第一个颜色值的描述
                ProductResponse.ParameterValue colorValue = parameter.getParameterValues().get(0);
                if (colorValue.getParameterValueDescriptionsLangData() != null &&
                        !colorValue.getParameterValueDescriptionsLangData().isEmpty()) {
                    // 优先获取英文描述
                    return colorValue.getParameterValueDescriptionsLangData().stream()
                            .filter(desc -> "eng".equals(desc.getLangId()))
                            .map(ProductResponse.ParameterValueDescription::getParameterValueName)
                            .findFirst()
                            .orElseGet(() -> colorValue.getParameterValueDescriptionsLangData()
                                    .get(0).getParameterValueName());
                }
            }
        }
        return null;
    }

    /**
     * 计算实际可用库存
     */
    private int calculateAvailableStock(ProductResponse.ProductSizeData sizeData) {
        Integer quantity = sizeData.getProductSizeQuantity();
        if (quantity == null) return 0;

        ProductResponse.ProductSizeReservations reservations = sizeData.getProductSizeReservations();
        if (reservations == null) return quantity;

        int reserved = Stream.of(
                        reservations.getProductSizeReservationAdhoc(),
                        reservations.getProductSizeReservationAuction(),
                        reservations.getProductSizeReservationClient(),
                        reservations.getProductSizeReservationOrder(),
                        reservations.getProductSizeReservationRetail(),
                        reservations.getProductSizeReservationWholesale()
                )
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        return Math.max(0, quantity - reserved);
    }

//    /**
//     * IdoSell分类ID转换为ERP分类ID
//     * 需要维护一个分类映射表
//     */
//    private Long convertToErpCategoryId(Long idoSellCategoryId) {
//        // 这里应该查询分类映射表获取对应的ERP分类ID
//        // 示例实现
//        Map<Long, Long> categoryMapping = new HashMap<>();
//        categoryMapping.put(1214554016L, 3L); // 靴子
//        categoryMapping.put(1214554015L, 4L); // 靴子
//
//        Long erpCategoryId = categoryMapping.get(idoSellCategoryId);
//        if (erpCategoryId == null) {
//            log.warn("未找到分类映射, idoSellCategoryId: {}", idoSellCategoryId);
//            // 可以返回一个默认分类ID，或者抛出异常
//            return 1L; // 默认分类
//        }
//
//        return erpCategoryId;
//    }
//
//    /**
//     * IdoSell颜色ID转换为ERP颜色ID
//     * 需要维护一个颜色映射表
//     */
//    private Long convertToErpColorId(String idoSellColorId) {
//        // 这里应该查询颜色映射表获取对应的ERP颜色ID
//        // 示例实现
//        Map<String, Long> colorMapping = new HashMap<>();
//        colorMapping.put("Black", 1L);
//        colorMapping.put("Brown", 15L);
//        colorMapping.put("Gold", 12L);
//        colorMapping.put("Grey", 16L);
//        colorMapping.put("Beige", 17L);//米色
//        colorMapping.put("Pink", 18L);
//        colorMapping.put("Blue", 19L);
//        colorMapping.put("White", 2L);
//        colorMapping.put("Silver", 20L);
//        colorMapping.put("Rose gold", 21L);
//        colorMapping.put("Navy blue", 22L);
//        colorMapping.put("Green", 23L);
//        colorMapping.put("Purple", 24L);
//        colorMapping.put("Champagne", 25L);
//        colorMapping.put("Khaki", 9L);
//
//        colorMapping.put("Orange", 37L);
//        colorMapping.put("Red", 38L);
//        colorMapping.put("Multicolour", 39L);
//        colorMapping.put("Yellow", 40L);
//        colorMapping.put("Burgundy", 41L);
//
//
//        Long erpColorId = colorMapping.get(idoSellColorId);
//        if (erpColorId == null) {
//            log.warn("未找到颜色映射, idoSellColorId: {}", idoSellColorId);
//            return 1L; // 默认颜色
//        }
//
//        return erpColorId;
//    }
//
//    /**
//     * IdoSell尺码ID转换为ERP尺码ID
//     * 需要维护一个尺码映射表
//     */
//    private Long convertToErpSizeId(String idoSellSizeId) {
//        // 这里应该查询尺码映射表获取对应的ERP尺码ID
//        // 示例实现
//        Map<String, Long> sizeMapping = new HashMap<>();
//        sizeMapping.put("19", 3L);//EUR36
//        sizeMapping.put("21", 4L);//EUR37
//        sizeMapping.put("B", 5L);//EUR38
//        sizeMapping.put("D", 11L);//EUR39
//        sizeMapping.put("F", 13L);//EUR40
//        sizeMapping.put("H", 14L);//EUR41
//
//        sizeMapping.put("J", 26L);//EUR42
//        sizeMapping.put("X", 27L);//EUR30
//        sizeMapping.put("Y", 28L);//EUR31
//        sizeMapping.put("Z", 29L);//EUR32
//        sizeMapping.put("20", 30L);//EUR33
//        sizeMapping.put("22", 31L);//EUR34
//        sizeMapping.put("23", 32L);//EUR35
//        sizeMapping.put("33", 33L);//EUR35-36
//        sizeMapping.put("82", 34L);//EUR37-38
//        sizeMapping.put("83", 35L);//EUR39-40
//        sizeMapping.put("84", 36L);//EUR41-42
//
//        sizeMapping.put("18", 32L);//EUR35
//        sizeMapping.put("L", 42L);//EUR43
//        sizeMapping.put("N", 43L);//EUR44
//        sizeMapping.put("P", 44L);//EUR45
//        sizeMapping.put("R", 45L);//EUR46
//
//        Long erpSizeId = sizeMapping.get(idoSellSizeId);
//        if (erpSizeId == null) {
//            log.warn("未找到尺码映射, idoSellSizeId: {}", idoSellSizeId);
//            // 默认尺码
//            return 13L;
//        }
//
//        return erpSizeId;
//    }

    /**
     * 生成唯一的SKU ID
     */
    private String generateUniqueSkuId(Integer productId, String sizeId) {
        if (productId == null || sizeId == null) {
            throw new IllegalArgumentException("productId 和 sizeId 不能为空");
        }
        return String.format("%d_%s", productId, sizeId);
    }

}
