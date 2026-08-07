package cn.qihangerp.open.shein.service.impl;

import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.utils.ImageUtils;

import cn.qihangerp.domain.OShop;
import cn.qihangerp.domain.OShopPlatform;
import cn.qihangerp.module.goods.domain.*;
import cn.qihangerp.module.goods.mapper.OGoodsPublishMapper;
import cn.qihangerp.oms.service.*;
import cn.qihangerp.open.idosell.utils.PartNumberUtils;
import cn.qihangerp.module.open.shein.domain.*;
import cn.qihangerp.open.shein.helper.SheinApiHelper;
import cn.qihangerp.open.shein.helper.SheinGoodsApiHelper;
import cn.qihangerp.open.shein.helper.model.*;
import cn.qihangerp.open.shein.helper.model.*;
import cn.qihangerp.open.shein.helper.response.SheinProductDetailResponse;
import cn.qihangerp.module.open.shein.mapper.OmsSheinGoodsMapper;
import cn.qihangerp.module.open.shein.mapper.OmsSheinGoodsSkuMapper;
import cn.qihangerp.open.shein.service.*;

import cn.qihangerp.open.shein.service.*;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SheinGoodsCommonServiceImpl implements SheinGoodsCommonService {

    private final OmsSheinGoodsMapper sheinGoodsMapper;
    private final OGoodsPublishMapper goodsPublishMapper;
    private final OmsSheinGoodsService sheinGoodsService;
    private final OmsSheinGoodsSkuMapper sheinGoodsSkuMapper;
    private final OGoodsService oGoodsService;
    private final OGoodsSkuService oGoodsSkuService;
    private final OGoodsAttributeValueService goodsAttributeValueService;
    private final OGoodsAttributeService goodsAttributeService;
    private final OShopPlatformService platformService;
    private final ApplicationContext applicationContext;
    private final OmsSheinShopCategoryService sheinShopCategoryService;
    private final OmsSheinProductTypeAttrService sheinProductTypeAttrService;
    private final OmsSheinProductAttrService sheinProductAttrService;
    private final OmsSheinProductAttrValService sheinProductAttrValService;
    private final OGoodsInventoryService goodsInventoryService;
    private final OShopService shopService;

    /**
     * 拉取Shein商品
     * @param shopId
     * @param appKey
     * @param appSecret
     * @param url
     */
    @Override
    public void pullProductAndSkuList(Long shopId,String appKey, String appSecret, String url) throws InterruptedException {
        log.info("=======开始拉取Shein商品=========...");
        final int PAGE_SIZE = 50; // ?
        int currentPage = 1;
        boolean hasMorePages = true;

        // 
        while (hasMorePages) {
            log.info(" 开始读�?�?{} 页数据：", currentPage);
            try {
                // 
                ProductListRequest request = new ProductListRequest();
                request.setPageNum(currentPage);
                request.setPageSize(PAGE_SIZE);
                request.setAppKey(appKey);
                request.setAppSecret(appSecret);
                request.setUrl(url);
                // 
                ProductListResponse response = SheinGoodsApiHelper.queryProductList(request);

                if (response.getInfo() != null && response.getInfo().getData() != null) {
                    log.info("商品列表查询结果：{}", response.getInfo().getData().size());
                    //  spuName 字段去重
                    List<ProductListResponse.ProductInfo> uniqueProductList = response.getInfo().getData().stream()
                            .collect(Collectors.toMap(
                                    ProductListResponse.ProductInfo::getSpuName,
                                    product -> product,
                                    (existing, replacement) -> existing
                            ))
                            .values()
                            .stream()
                            .collect(Collectors.toList());
                    log.info("spuName not found in {}", uniqueProductList.size());
                    for (ProductListResponse.ProductInfo product : uniqueProductList) {
//                        log.info("----------------------------------------");
//                        log.info("SPU名称: {}", product.getSpuName());
//                        log.info("SKC名称: {}", product.getSkcName());
                        SheinProductDetailResponse detailResponse = SheinGoodsApiHelper.queryProductDetail(product.getSpuName(), url, appKey, appSecret);
                        if (detailResponse != null && detailResponse.getInfo() != null) {
                            if (detailResponse.getInfo().getSkcInfoList() == null || detailResponse.getInfo().getSkcInfoList().size() == 0) {
                                log.error("=======没有找到skc数据========{}", product.getSpuName());
                                continue;
                            }

                            OmsSheinGoods sheinGoods = new OmsSheinGoods();
                            sheinGoods.setSpuName(product.getSpuName());
                            sheinGoods.setProductTypeId(detailResponse.getInfo().getProductTypeId());
                            sheinGoods.setCategoryId(detailResponse.getInfo().getCategoryId());
                            sheinGoods.setBrandCode(detailResponse.getInfo().getBrandCode());

                            String goodsNum = PartNumberUtils.extractPartNumber(detailResponse.getInfo().getSupplierCode());
                            sheinGoods.setSupplierCode(goodsNum);
                            // ?
                            if (!detailResponse.getInfo().getProductMultiNameList().isEmpty()) {
                                SheinProductDetailResponse.InfoDTO.ProductMultiNameListDTO en = detailResponse.getInfo().getProductMultiNameList().stream().filter(x -> x.getLanguage().equals("en"))
                                        .findFirst()
                                        .orElseGet(() -> detailResponse.getInfo().getProductMultiNameList().isEmpty() ? null : detailResponse.getInfo().getProductMultiNameList().get(0));
                                if (en != null) {
                                    sheinGoods.setProductName(en.getProductName());
                                }
                            }
                            sheinGoods.setDimensionAttributeInfoList(JSONObject.toJSONString(detailResponse.getInfo().getDimensionAttributeInfoList()));
                            sheinGoods.setProductAttributeInfoList(JSONObject.toJSONString(detailResponse.getInfo().getProductAttributeInfoList()));
                            sheinGoods.setProductMultiDescList(JSONObject.toJSONString(detailResponse.getInfo().getProductMultiDescList()));
                            sheinGoods.setProductMultiNameList(JSONObject.toJSONString(detailResponse.getInfo().getProductMultiNameList()));
                            sheinGoods.setSpuImageInfolist(JSONObject.toJSONString(detailResponse.getInfo().getSpuImageInfoList()));
                            if (detailResponse.getInfo().getSpuImageInfoList() != null) {
                                sheinGoods.setProductImage(detailResponse.getInfo().getSpuImageInfoList().get(0).getImageUrl());
                            } else {
                                if (detailResponse.getInfo().getSkcInfoList() != null && detailResponse.getInfo().getSkcInfoList().get(0).getSkcImageInfoList() != null) {
                                    sheinGoods.setProductImage(detailResponse.getInfo().getSkcInfoList().get(0).getSkcImageInfoList().get(0).getImageUrl());
                                } else {
                                    if (detailResponse.getInfo().getSkcInfoList().get(0).getSkuInfoList() != null) {
                                        if (detailResponse.getInfo().getSkcInfoList().get(0).getSkuInfoList().get(0).getSkuImageInfoList() != null) {
                                            sheinGoods.setProductImage(detailResponse.getInfo().getSkcInfoList().get(0).getSkuInfoList().get(0).getSkuImageInfoList().get(0).getImageUrl());
                                        }
                                    }
                                }
                            }
                            Long oGoodsId = 0L;
                            // ?
                            List<OGoods> oGoodsList = oGoodsService.list(new LambdaQueryWrapper<OGoods>().eq(OGoods::getGoodsNum, sheinGoods.getSupplierCode()));
                            if (!oGoodsList.isEmpty()) {
                                oGoodsId = oGoodsList.get(0).getId();
                                log.info("==========找到了OGoods========{}", oGoodsId);
                            } else {
                                log.error("==========没有找到OGoods信息========{}", sheinGoods.getSupplierCode());
                            }
                            sheinGoods.setOGoodsId(oGoodsId);
                            // ?
                            List<String> detailImages = new ArrayList<>();
                            // sku
                            for (var skc : detailResponse.getInfo().getSkcInfoList()) {
                                if(skc.getSiteDetailImageInfoList()!=null&& skc.getSiteDetailImageInfoList().size()>0){
                                    for(var skcDetailImage:skc.getSiteDetailImageInfoList()){
                                        for(var detailImage:skcDetailImage.getImageInfoList()) {
                                            detailImages.add(detailImage.getImageUrl());
                                        }
                                    }
                                    sheinGoods.setDetailImages(String.join(",", detailImages));
                                }
                                for (var sku : skc.getSkuInfoList()) {
                                    OmsSheinGoodsSku goodsSku = new OmsSheinGoodsSku();
                                    goodsSku.setOGoodsId(oGoodsId);
                                    goodsSku.setProductName(sheinGoods.getProductName());
                                    goodsSku.setSpuName(sheinGoods.getSpuName());
                                    goodsSku.setSkcName(skc.getSkcName());
                                    goodsSku.setSkuCode(sku.getSkuCode());
                                    goodsSku.setSupplierCode(skc.getSupplierCode());
                                    goodsSku.setSupplierSku(sku.getSupplierSku());
                                    goodsSku.setColorId(skc.getAttributeId().toString());
                                    goodsSku.setColorValueId(skc.getAttributeValueId().toString());
                                    goodsSku.setSkcImage(JSONObject.toJSONString(skc.getSkcImageInfoList()));
                                    if (!skc.getSkcImageInfoList().isEmpty()) {
                                        if (!StringUtils.hasText(sheinGoods.getProductImage())) {
                                            sheinGoods.setProductImage(skc.getSkcImageInfoList().get(0).getImageUrl());
                                        }
                                        goodsSku.setColorImage(skc.getSkcImageInfoList().get(0).getImageUrl());
                                    } else {
                                        if (StringUtils.hasText(sheinGoods.getProductImage())) {
                                            goodsSku.setColorImage(sheinGoods.getProductImage());
                                        }
                                    }
                                    if (!skc.getAttributeValueMultiList().isEmpty()) {
                                        List<SheinProductDetailResponse.InfoDTO.SkcInfoListDTO.AttributeValueMultiListDTO> en = skc.getAttributeValueMultiList().stream().filter(x -> x.getLanguage().equals("en")).collect(Collectors.toList());
                                        if (!en.isEmpty()) {
                                            goodsSku.setColorValue(en.get(0).getAttributeValueName());
                                        }
                                    }
                                    goodsSku.setColorAttribute(JSONObject.toJSONString(skc.getAttributeMultiList()));
                                    goodsSku.setColorAttributeValue(JSONObject.toJSONString(skc.getAttributeValueMultiList()));
                                    goodsSku.setSaleAttributeList(JSONObject.toJSONString(sku.getSaleAttributeList()));
                                    if (sku.getSaleAttributeList() != null && !sku.getSaleAttributeList().isEmpty()) {
                                        goodsSku.setSizeId(sku.getSaleAttributeList().get(0).getAttributeId().toString());
                                        goodsSku.setSizeValueId(sku.getSaleAttributeList().get(0).getAttributeValueId().toString());
                                        if (!sku.getSaleAttributeList().get(0).getAttributeValueMultiList().isEmpty()) {
                                            var en = sku.getSaleAttributeList().get(0).getAttributeValueMultiList().stream().filter(x -> x.getLanguage().equals("en")).collect(Collectors.toList());
                                            if (!en.isEmpty()) {
                                                goodsSku.setSizeValue(en.get(0).getAttributeValueName());
                                            }
                                        }
                                    }
                                    goodsSku.setWeight(sku.getWeight().toString());
                                    goodsSku.setHeight(sku.getHeight().toString());
                                    goodsSku.setWidth(sku.getWidth().toString());
                                    goodsSku.setLength(sku.getLength().toString());
                                    goodsSku.setShelfStatusInfoList(JSONObject.toJSONString(skc.getShelfStatusInfoList()));
                                    goodsSku.setMallState(sku.getMallState());
                                    goodsSku.setStopPurchase(sku.getStopPurchase());
                                    goodsSku.setCostInfoList(JSONObject.toJSONString(sku.getCostInfoList()));
                                    if (sku.getCostInfoList() != null && !sku.getCostInfoList().isEmpty()) {
                                        List<SheinProductDetailResponse.InfoDTO.SkcInfoListDTO.SkuInfoListDTO.CostInfoListDTO> cny = sku.getCostInfoList().stream().filter(x -> x.getCurrency().equals("CNY")).collect(Collectors.toList());
                                        if (cny != null && !cny.isEmpty()) {
                                            goodsSku.setCostPriceCny(BigDecimal.valueOf(cny.get(0).getCostPrice()));
                                            if(sheinGoods.getCostPriceCny()==null) {
                                                sheinGoods.setCostPriceCny(BigDecimal.valueOf(cny.get(0).getCostPrice()));
                                            }
                                        }
                                        List<SheinProductDetailResponse.InfoDTO.SkcInfoListDTO.SkuInfoListDTO.CostInfoListDTO> eur = sku.getCostInfoList().stream().filter(x -> x.getCurrency().equals("EUR")).collect(Collectors.toList());
                                        if (eur != null && !eur.isEmpty()) {
                                            goodsSku.setCostPriceEur(BigDecimal.valueOf(eur.get(0).getCostPrice()));
                                            if(sheinGoods.getCostPriceEur()==null) {
                                                sheinGoods.setCostPriceEur(BigDecimal.valueOf(eur.get(0).getCostPrice()));
                                            }
                                        }
                                    }

                                    // goodssku
                                    Long oGoodsSkuId = 0L;
                                    // oGoodsId+omsColorValueId+omsSizeValueId
                                    Long omsColorValueId = 0L;
                                    Long omsSizeValueId = 0L;
                                    // colorid查找属性�?
                                    List<OmsSheinProductAttrVal> sheinColorVals = sheinProductAttrValService.list(
                                            new LambdaQueryWrapper<OmsSheinProductAttrVal>()
                                                    .eq(OmsSheinProductAttrVal::getAttributeValueId, skc.getAttributeValueId()));
                                    if (sheinColorVals.isEmpty()) {
                                        log.error("==========找不到skc属性值id数据=========");
                                    } else {
                                        if (sheinColorVals.get(0).getOmsAttributeValueId() == null || sheinColorVals.get(0).getOmsAttributeValueId() == 0) {
                                            log.error("==========Shein商品属性值id未关联oms属性值id=======");
                                        } else {
                                            omsColorValueId = sheinColorVals.get(0).getOmsAttributeValueId();
                                        }
                                    }
                                    List<OmsSheinProductAttrVal> sheinSizeVals = sheinProductAttrValService.list(
                                            new LambdaQueryWrapper<OmsSheinProductAttrVal>()
                                                    .eq(OmsSheinProductAttrVal::getAttributeValueId, goodsSku.getSizeValueId()));
                                    if (sheinSizeVals.isEmpty()) {
                                        log.error("==========找不到sku size属性值id数据=========");
                                    } else {
                                        if (sheinSizeVals.get(0).getOmsAttributeValueId() == null || sheinSizeVals.get(0).getOmsAttributeValueId() == 0) {
                                            log.error("==========Shein商品size属性值id未关联oms属性值id=======");
                                        } else {
                                            omsSizeValueId = sheinSizeVals.get(0).getOmsAttributeValueId();
                                        }
                                    }

                                    List<OGoodsSku> oGoodsSkuList = oGoodsSkuService.list(
                                            new LambdaQueryWrapper<OGoodsSku>()
                                                    .eq(OGoodsSku::getGoodsNum, sheinGoods.getSupplierCode())
                                                    .eq(OGoodsSku::getColorValueId, omsColorValueId)
                                                    .eq(OGoodsSku::getSizeValueId, omsSizeValueId)
                                    );
                                    if (oGoodsSkuList.isEmpty()) {
//                                            log.error("=========没有找到oGoodsSku数据=========={}-{}",sku.getSupplierSku(),sku.getSkuCode());
                                    } else {
                                        oGoodsSkuId = oGoodsSkuList.get(0).getId();
                                        log.info("==========找到了OGoodsSku========{}", oGoodsSkuId);
                                    }
                                    goodsSku.setOGoodsSkuId(oGoodsSkuId);
                                    List<OmsSheinGoodsSku> omsSheinGoodsSkus = sheinGoodsSkuMapper.selectList(new LambdaQueryWrapper<OmsSheinGoodsSku>().eq(OmsSheinGoodsSku::getSkuCode, goodsSku.getSkuCode()));
                                    if (omsSheinGoodsSkus.isEmpty()) {
                                        // 
                                        goodsSku.setCreateTime(new Date());
                                        sheinGoodsSkuMapper.insert(goodsSku);
                                        log.info("==============拉取添加SHEIN商品GoodsSKU======== {} ", goodsSku.getSkuCode());
                                    } else {
                                        goodsSku.setId(omsSheinGoodsSkus.get(0).getId());
                                        goodsSku.setUpdateTime(LocalDateTime.now());
                                        sheinGoodsSkuMapper.updateById(goodsSku);
                                        log.info("==============拉取修改SHEIN商品GoodsSKU======== {} ", goodsSku.getSkuCode());
                                    }
                                }
                            }


                            LambdaQueryWrapper<OmsSheinGoods> queryWrapper = new LambdaQueryWrapper<>(OmsSheinGoods.class).eq(OmsSheinGoods::getSpuName, product.getSpuName());
                            OmsSheinGoods exist = sheinGoodsMapper.selectOne(queryWrapper);
                            if (exist != null) {
                                // ?
                                sheinGoods.setId(exist.getId());
                                sheinGoods.setUpdateTime(LocalDateTime.now());
                                sheinGoodsMapper.updateById(sheinGoods);
                                log.info("============拉取更新SHEIN商品======== {} ", sheinGoods.getSpuName());
                            } else {
                                // ?
                                sheinGoods.setShopId(shopId);
                                sheinGoods.setCreateTime(new Date());
                                sheinGoodsMapper.insert(sheinGoods);
                                log.info("================拉取添加SHEIN商品======== {} ", sheinGoods.getSpuName());
                            }

                        }
                    }
                    // ?
                    hasMorePages = true;
                    currentPage++;
                    // ?
                    Thread.sleep(1000);
                } else {
                    log.info("未查询到商品数据");
                    // ?
                    hasMorePages = false;
                }

            } catch (IOException e) {
                // ?
                Thread.sleep(1000);
                log.error("查询商品列表失败", e);
            }
        }
        log.info("========拉取Shein商品完成=========");
    }

    /**
     * 商品库发布商品到Shein
     * @param goodsId
     * @param shopId
     */
    @Override
    public ResultVo publishToShein(Long goodsId, Long shopId) {

        List<OmsSheinGoods> localSheinGoodsList = sheinGoodsService.list(new LambdaQueryWrapper<>(OmsSheinGoods.class).eq(OmsSheinGoods::getOGoodsId, goodsId));

        if (CollectionUtils.isNotEmpty(localSheinGoodsList)) {
            log.info("已经同步过了，则执行更新操作");
            //this.againSyncGoods(goodsId,shopType,localSheinGoodsList);
            return ResultVo.error("已经同步过了");
        }

        OShopPlatform platform = platformService.selectById(EnumShopType.SHEIN.getIndex());
        String appKey = platform.getAppKey();
        String appSecret = platform.getAppSecret();
        String serverUrl = platform.getServerUrl();
        OShop shop = shopService.getById(shopId);
        if (shop == null) {
            log.info("没有找到Shein店铺信息");
            return ResultVo.error("没有找到Shein店铺信息");
        }
        log.info("=======开始发布商品到{}{}", shop.getId(), shop.getName());
        OGoods oGoods = oGoodsService.selectGoodsById(Long.valueOf(goodsId));
        // ?
        List<OmsSheinShopCategory> shopCategories = sheinShopCategoryService.list(
                new LambdaQueryWrapper<OmsSheinShopCategory>()
                        .eq(OmsSheinShopCategory::getOmsCategoryId, oGoods.getCategoryId()));
        if (shopCategories == null || shopCategories.isEmpty()) {
            log.info("没有找到对应的Shein分类信息");
            return ResultVo.error("没有找到对应的Shein分类信息");
        }
        OmsSheinShopCategory shopCategory = shopCategories.get(0);
        List<OGoodsSku> goodsSkuList = oGoodsSkuService.list(new LambdaQueryWrapper<>(OGoodsSku.class).eq(OGoodsSku::getGoodsId, goodsId));

        try {
            // 
            ProductPublishRequest request = new ProductPublishRequest();
            request.setAppKey(appKey);
            request.setAppSecret(appSecret);
            request.setUrl(serverUrl);
            request.setCategoryId(shopCategory.getCategoryId());
            request.setProductTypeId(shopCategory.getProductTypeId());
            request.setSourceSystem("QiJi");  // ?
            //request.setBrandId(456L);
            request.setSuitFlag(0); // 
            request.setSupplierCode(oGoods.getGoodsNum());  // 
            List<ProductPublishRequest.SiteInfo> siteInfos = new ArrayList<>();
            ProductPublishRequest.SiteInfo siteInfo = new ProductPublishRequest.SiteInfo();
            siteInfo.setMainSite("shein");
            String[] subSites = new String[]{
                    "shein-fr",
                    "shein-es",
                    "shein-de",
                    "shein-it",
                    "shein-nl",
                    "shein-se",
                    "shein-pl",
                    "shein-pt",
                    "shein-euqs",
                    "shein-ro",
                    "shein-at"};
            siteInfo.setSubSiteList(Arrays.stream(subSites).toList());
            siteInfos.add(siteInfo);
            request.setSiteList(siteInfos);

            // 
            List<ProductPublishRequest.MultiLanguageInfo> nameList = new ArrayList<>();
            ProductPublishRequest.MultiLanguageInfo zhName = new ProductPublishRequest.MultiLanguageInfo();
            String language = "zh-cn";
            zhName.setLanguage(language);
            zhName.setName(oGoods.getName());
            nameList.add(zhName);// 
            ProductPublishRequest.MultiLanguageInfo enName = new ProductPublishRequest.MultiLanguageInfo();
            enName.setLanguage("en");
            enName.setName(oGoods.getName());  // 
            nameList.add(enName);

            request.setMultiLanguageNameList(nameList);

            List<ProductPublishRequest.MultiLanguageInfo> descList = new ArrayList<>();
            ProductPublishRequest.MultiLanguageInfo zhDesc = new ProductPublishRequest.MultiLanguageInfo();
            zhDesc.setLanguage(language);
            zhDesc.setDescription(oGoods.getName());
            descList.add(zhDesc);
            request.setMultiLanguageDescList(descList);

            // ?
            List<ProductPublishRequest.ProductAttribute> attributes = new ArrayList<>();
            // 
            List<OmsSheinProductTypeAttr> productTypeAttrList = sheinProductTypeAttrService.list(
                    new LambdaQueryWrapper<OmsSheinProductTypeAttr>()
                            .eq(OmsSheinProductTypeAttr::getProductTypeId, shopCategory.getProductTypeId())
                            .eq(OmsSheinProductTypeAttr::getAttributeStatus, 3)//?
            );
            if (productTypeAttrList != null && productTypeAttrList.size() > 0) {
                for (var at : productTypeAttrList) {
                    ProductPublishRequest.ProductAttribute attr2 = new ProductPublishRequest.ProductAttribute();
                    attr2.setAttributeId(at.getAttributeId());
                    List<OmsSheinProductAttrVal> values = sheinProductAttrValService.list(
                            new LambdaQueryWrapper<OmsSheinProductAttrVal>()
                                    .eq(OmsSheinProductAttrVal::getAttributeId, at.getAttributeId())
                                    .last(" order by is_default desc limit 1")
                    );
                    attr2.setAttributeValueId(values.get(0).getAttributeValueId());

                    attributes.add(attr2);
                }

            }
            // ?
            if (attributes.size() > 0) {
                request.setProductAttribute(attributes);
            }


            // ?
            Map<Long, List<OGoodsSku>> skcList = goodsSkuList.stream().collect(Collectors.groupingBy(OGoodsSku::getColorValueId));

            // SKC信息
            List<ProductPublishRequest.SkcInfo> skcInfos = new ArrayList<>();
            String image = oGoods.getImage();
            String[] images = image.split(",");

//            goodsGroupByColorId.forEach((colorValueId, skuList) -> {
            for (Map.Entry<Long, List<OGoodsSku>> entry : skcList.entrySet()) {
                Long colorValueId = entry.getKey();
                List<OGoodsSku> skuList = entry.getValue();

                ProductPublishRequest.SkcInfo skcInfo = new ProductPublishRequest.SkcInfo();
                // skc属�?
                OGoodsAttributeValue colorValue = goodsAttributeValueService.getById(colorValueId);
                if (colorValue == null) {
                    log.info("No color attribute value found");
                    return ResultVo.error("No color attribute value found");
                }
                List<OmsSheinProductAttrVal> sheinProductAttrVals = sheinProductAttrValService.list(new LambdaQueryWrapper<OmsSheinProductAttrVal>().eq(OmsSheinProductAttrVal::getOmsAttributeValueId, colorValueId));
                if (sheinProductAttrVals == null || sheinProductAttrVals.isEmpty()) {
                    log.info("No Shein SKC attribute found");
                    return ResultVo.error("No Shein SKC attribute found");
                }

//                List<OmsSheinProductAttr> sheinProductAttrs = sheinProductAttrService.list(new LambdaQueryWrapper<OmsSheinProductAttr>().eq(OmsSheinProductAttr::getOmsAttributeId, colorId));
//                if(sheinProductAttrs==null||sheinProductAttrs.isEmpty()){
//                    log.info("No Shein SKC attribute found");
//                    return;
//                }

                // ?
                ProductPublishRequest.SaleAttribute saleMinAttr = new ProductPublishRequest.SaleAttribute();
//                saleMinAttr.setAttributeId(Long.valueOf(saleSkcAttributeMapping.getAttributeId()));
//                saleMinAttr.setAttributeValueId(Long.valueOf(skcMapping.get(String.valueOf(colorId)).getAttributeValueId()));

                saleMinAttr.setAttributeId(sheinProductAttrVals.get(0).getAttributeId());
                saleMinAttr.setAttributeValueId(sheinProductAttrVals.get(0).getAttributeValueId());


                skcInfo.setSaleAttribute(saleMinAttr);
                skcInfo.setSupplierCode(oGoods.getGoodsNum() + colorValue.getSkcCode());  // SKC商家货号


                // 
                ProductPublishRequest.ImageInfo imageInfo = new ProductPublishRequest.ImageInfo();
                List<ProductPublishRequest.ImageInfoItem> imageInfoList = new ArrayList<>();

                // 
                ProductPublishRequest.ImageInfoItem mainImage = new ProductPublishRequest.ImageInfoItem();
                mainImage.setImageType(1); // 1: 主图
                mainImage.setImageSort(1);
                String mainSourceImageUrl = images[0];

                String mainImageUrl = null;
                File mainImageFile = null;
                try {
                    mainImageFile = ImageUtils.resizeImageFile(mainSourceImageUrl, 1340, 1785);
                    ImageUploadResponse mainImageResponse = SheinApiHelper.uploadImage(appKey, appSecret, serverUrl, mainImageFile, 1L); // 1: 主图
                    mainImageUrl = mainImageResponse.getInfo().getImageUrl();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    // 
                    if (mainImageFile != null && mainImageFile.exists()) {
                        boolean deleted = mainImageFile.delete();
                        if (!deleted) {
                            log.warn("临时文件删除失败: {}", mainImageFile.getAbsolutePath());
                        }
                    }
                }

                String detailImageUrl = null;
                File detailImageFile = null;
                try {
                    String detailSourceImageUrl = mainSourceImageUrl;
                    if (images.length >= 2) {
                        detailSourceImageUrl = images[1];
                    }
                    detailImageFile = ImageUtils.resizeImageFile(detailSourceImageUrl, 1340, 1785);
                    ImageUploadResponse detailImageResponse = SheinApiHelper.uploadImage(appKey, appSecret, serverUrl, detailImageFile, 2L); // 2: 细节�?
                    detailImageUrl = detailImageResponse.getInfo().getImageUrl();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    // 
                    if (detailImageFile != null && detailImageFile.exists()) {
                        boolean deleted = detailImageFile.delete();
                        if (!deleted) {
                            log.warn("临时文件删除失败: {}", detailImageFile.getAbsolutePath());
                        }
                    }
                }

                mainImage.setImageUrl(mainImageUrl);
                imageInfoList.add(mainImage);

                // ?
                ProductPublishRequest.ImageInfoItem detailImage = new ProductPublishRequest.ImageInfoItem();
                detailImage.setImageType(2); // 2: 细节�?
                detailImage.setImageSort(2);
                detailImage.setImageUrl(detailImageUrl);
                imageInfoList.add(detailImage);

                // ?
                ProductPublishRequest.ImageInfoItem squareImage = new ProductPublishRequest.ImageInfoItem();
                squareImage.setImageType(5); // 5: 方块�?
                squareImage.setImageSort(3);
                squareImage.setImageUrl(mainImageUrl);
                imageInfoList.add(squareImage);

                // SKC，需要添加色块图
                ProductPublishRequest.ImageInfoItem colorImage = new ProductPublishRequest.ImageInfoItem();
                colorImage.setImageType(6); // 6: 色块�?
                colorImage.setImageSort(4);


                String colorImageUrl = null;
                File colorImageFile = null;
                try {
                    String colorSourceImageUrl = mainSourceImageUrl;
                    if (images.length >= 3) {
                        colorSourceImageUrl = images[2];
                    }
                    colorImageFile = ImageUtils.resizeImageFile(colorSourceImageUrl, 80, 80);
                    ImageUploadResponse detailImageResponse = SheinApiHelper.uploadImage(appKey, appSecret, serverUrl, colorImageFile, 6L); // 2: 细节�?
                    colorImageUrl = detailImageResponse.getInfo().getImageUrl();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    // 
                    if (colorImageFile != null && colorImageFile.exists()) {
                        boolean deleted = colorImageFile.delete();
                        if (!deleted) {
                            log.warn("临时文件删除失败: {}", colorImageFile.getAbsolutePath());
                        }
                    }
                }

                colorImage.setImageUrl(colorImageUrl);
                imageInfoList.add(colorImage);

                imageInfo.setImageInfoList(imageInfoList);
                skcInfo.setImageInfo(imageInfo);
                //skcInfo.setSkcTitle(oGoods.getName());


                // SKU信息
                List<ProductPublishRequest.SkuInfo> skuInfos = new ArrayList<>();

                for (OGoodsSku goodsSku : skuList) {
                    // ?
                    OGoodsAttributeValue sizeValue = goodsAttributeValueService.getById(goodsSku.getSizeValueId());
                    if (sizeValue == null) {
                        log.info("No size attribute value found");
                        return ResultVo.error("No size attribute value found");
                    }
                    List<OmsSheinProductAttrVal> sheinSizeProductAttrVals = sheinProductAttrValService.list(
                            new LambdaQueryWrapper<OmsSheinProductAttrVal>()
                                    .eq(OmsSheinProductAttrVal::getOmsAttributeValueId, sizeValue.getAttributeValueId()));

                    if (sheinSizeProductAttrVals == null || sheinSizeProductAttrVals.isEmpty()) {
                        log.info("没有找到对应的Shein Size属性信");
                        return ResultVo.error("没有找到对应的Shein Size属性信");
                    }
                    ProductPublishRequest.SkuInfo skuInfo = new ProductPublishRequest.SkuInfo();
                    if (StringUtils.hasText(goodsSku.getSkuCode())) {
                        skuInfo.setSupplierSku(goodsSku.getSkuCode());  // SKU商家编码
                    } else {
                        skuInfo.setSupplierSku(goodsSku.getId().toString());
                    }

                    skuInfo.setMallState(1);  // 
                    skuInfo.setStopPurchase(1);  // 

                    ProductPublishRequest.CostInfo costInfo = new ProductPublishRequest.CostInfo();
                    costInfo.setCostPrice(goodsSku.getPurPrice());
//                    costInfo.setCurrency(shopConfigMapping.getCurrency());
                    // todo:写死�?
                    costInfo.setCurrency("EUR");
                    skuInfo.setCostInfo(costInfo);

                    // 
                    List<ProductPublishRequest.PriceInfo> priceInfos = new ArrayList<>();

//                    List<String> subSiteList = shopConfigMapping.getSubSiteList();

//                    for (String subSite : subSiteList) {
//                        ProductPublishRequest.PriceInfo priceInfo = new ProductPublishRequest.PriceInfo();
//                        priceInfo.setBasePrice(goodsSku.getPurPrice()==null?oGoods.getPurPrice());
//                        priceInfo.setCurrency(shopCategory.getCurrency());
//                        priceInfo.setSubSite(subSite);
//                        priceInfos.add(priceInfo);
//                    }
//                    skuInfo.setPriceInfoList(priceInfos);

                    // 
                    List<ProductPublishRequest.StockInfo> stockInfos = new ArrayList<>();
                    ProductPublishRequest.StockInfo stockInfo = new ProductPublishRequest.StockInfo();

                    OGoodsInventory goodsInventory = goodsInventoryService.getOne(new LambdaQueryWrapper<>(OGoodsInventory.class).eq(OGoodsInventory::getSkuId, goodsSku.getId()));
//                    if (goodsInventory == null || goodsInventory.getQuantity().intValue() <= 0) {
//                        log.info("更新商品信息 当前通skuCode {} 库存不足 ", goodsSku.getSkuCode());
//                        continue;
//                    }
                    stockInfo.setInventoryNum(goodsInventory != null ? goodsInventory.getQuantity().intValue() : 0);
                    stockInfos.add(stockInfo);
                    skuInfo.setStockInfoList(stockInfos);
                    List<ProductPublishRequest.SaleAttribute> saleAttributeList = new ArrayList<>();
                    ProductPublishRequest.SaleAttribute skuSaleAttribute = new ProductPublishRequest.SaleAttribute();

//                    skuSaleAttribute.setAttributeId(Long.valueOf(saleSkuAttributeMapping.getAttributeId()));
//                    skuSaleAttribute.setAttributeValueId(Long.valueOf(skuMapping.get(String.valueOf(goodsSku.getSizeId())).getAttributeValueId()));
                    skuSaleAttribute.setAttributeId(sizeValue.getAttributeId());
                    skuSaleAttribute.setAttributeValueId(sheinSizeProductAttrVals.get(0).getAttributeValueId());
                    saleAttributeList.add(skuSaleAttribute);
                    skuInfo.setSaleAttributeList(saleAttributeList);

                    // 
                    skuInfo.setHeight("15");
                    skuInfo.setLength("18");
                    skuInfo.setWidth("25");
                    skuInfo.setWeight("800");

                    skuInfos.add(skuInfo);
                }

                if (CollectionUtils.isEmpty(skuInfos)) {
                    return ResultVo.error("没有找到skuList信息");
                }

                skcInfo.setSkuInfo(skuInfos);


                skcInfos.add(skcInfo);

            }

            if (CollectionUtils.isEmpty(skcInfos)) {
                log.info("当前商品:{} 没有符合条件的skc列表，不进行同步", goodsId);
                return ResultVo.error("当前商品:" + goodsId + " 没有符合条件的skc列表，不进行同步");
            }

            request.setSkcInfo(skcInfos);

            // ?
            ProductPublishResponse response = SheinApiHelper.publishProduct(request);

            log.info("response:{}", JSON.toJSONString(response));

            // 
            if (!response.getCode().equals("0")) {
                log.error("发布失败：响应信�?{}", JSON.toJSONString(response));
                if (response.getInfo() != null && response.getInfo().getPreValidResult() != null) {
                    response.getInfo().getPreValidResult().forEach(validResult -> {
                        log.error("验证失败 - 模块: {}, 表单: {}, 消息: {}",
                                validResult.getModule(),
                                validResult.getForm(),
                                String.join(", ", validResult.getMessages())
                        );
                    });
                }
                return ResultVo.error("发布失败：响应信�?" + JSON.toJSONString(response));
            }

            log.info("Product published successfully功");
            log.info("SPU Name: {}", response.getInfo().getSpuName());

            if (response.getInfo().getSuccess()) {
                String spuName = response.getInfo().getSpuName();

                OmsSheinGoods sheinGoods = new OmsSheinGoods();
                sheinGoods.setSpuName(spuName);
                sheinGoods.setProductTypeId(request.getProductTypeId());
                sheinGoods.setCategoryId(request.getCategoryId());
                sheinGoods.setSupplierCode(request.getSupplierCode());
                // ?
                sheinGoods.setProductName(oGoods.getName());
                sheinGoods.setProductAttributeInfoList(JSONObject.toJSONString(request.getProductAttribute()));
                sheinGoods.setProductMultiDescList(JSONObject.toJSONString(request.getMultiLanguageDescList()));
                sheinGoods.setProductMultiNameList(JSONObject.toJSONString(request.getMultiLanguageNameList()));
                sheinGoods.setSpuImageInfolist(JSONObject.toJSONString(request.getSkcInfo()));
                sheinGoods.setProductImage(oGoods.getImage());
                sheinGoods.setProductImage(oGoods.getMainImage());
                sheinGoods.setOGoodsId(oGoods.getId());
                sheinGoods.setShopId(shop.getId());
                sheinGoods.setCreateTime(new Date());
                sheinGoodsMapper.insert(sheinGoods);
                // 
                OGoodsPublish publish = new OGoodsPublish();
                publish.setGoodsId(oGoods.getId());
                publish.setShopPlatformId(EnumShopType.SHEIN.getIndex());
                publish.setShopPlatform(EnumShopType.SHEIN.getName());
                publish.setShopId(shop.getId());
                publish.setShopName(shop.getName());
                publish.setPublishTime(new Date());
                goodsPublishMapper.insert(publish);

                log.info("====发布Shien商品成功添加到SHEIN店铺商品======== {} ", sheinGoods.getSpuName());

                // sku
                if (response.getInfo().getSkcList() != null) {
                    response.getInfo().getSkcList().forEach(skc ->
                            {
                                log.info("SKC Name: {}", skc.getSkcName());
                                List<ProductPublishResponse.SkuInfo> skuList = skc.getSkuList();
                                for (ProductPublishResponse.SkuInfo sku : skuList) {
                                    log.info("SKU Code: {}", sku.getSkuCode());
                                    log.info("Supplier SKU: {}", sku.getSupplierSku());

                                    OmsSheinGoodsSku goodsSku = new OmsSheinGoodsSku();
                                    goodsSku.setSupplierCode(sheinGoods.getSupplierCode());
                                    goodsSku.setProductName(sheinGoods.getProductName());
                                    goodsSku.setSpuName(sheinGoods.getSpuName());
                                    goodsSku.setSkcName(skc.getSkcName());
                                    goodsSku.setSkuCode(sku.getSkuCode());
                                    goodsSku.setOGoodsId(sheinGoods.getOGoodsId());
                                    goodsSku.setSupplierSku(sku.getSupplierSku());
                                    // oGoodsSkuId
                                    OGoodsSku oGoodsSku = oGoodsSkuService.getById(sku.getSupplierSku());
                                    if (oGoodsSku != null) {
                                        goodsSku.setOGoodsSkuId(oGoodsSku.getId());
                                    } else {
                                        List<OGoodsSku> oGoodsSkuList = oGoodsSkuService.list(new LambdaQueryWrapper<OGoodsSku>().eq(OGoodsSku::getSkuCode, sku.getSupplierSku()));
                                        if (oGoodsSkuList != null && oGoodsSkuList.size() > 0) {
                                            goodsSku.setOGoodsSkuId(oGoodsSkuList.get(0).getId());
                                        }
                                    }
                                    // 
                                    goodsSku.setCreateTime(new Date());
                                    sheinGoodsSkuMapper.insert(goodsSku);
                                    log.info("====发布Shein商品添加SHEIN店铺商品SKU======== {} ", goodsSku.getSkuCode());

                                }
//                                List<String> skuCodeList = skuList.stream().map(ProductPublishResponse.SkuInfo::getSkuCode).toList();
//                            this.syncSkuInfoToLocal(appKey, appSecret, serverUrl, skuCodeList);
                            }


                    );
                }
            }
            log.info("Success: {}", response.getInfo().getSuccess());
//            log.info("Version: {}", response.getInfo().getVersion());
//            log.info("Trace ID: {}", response.getTraceId());
            return ResultVo.success();
        } catch (IOException e) {
            log.error("商品发布失败", e);
            return ResultVo.error("商品发布失败" + e.getMessage());
        }
    }

//    @Override
//    public void batchSyncGoods() {
//        log.info("开始执行批量同步商�?..");
//        try {
//            // 
//            int pageSize = 10;  // 10条记�?
//            int pageNum = 1;
//            boolean hasMore = true;
//
//            while (hasMore) {
//                log.info("开始处理第 {} 页数�?, pageNum);
//
//                // 
//                Page<OGoods> page = new Page<>(pageNum, pageSize);
//                LambdaQueryWrapper<OGoods> queryWrapper = new LambdaQueryWrapper<OGoods>()
//                        .eq(OGoods::getCategoryId,4)
//                    .orderByAsc(OGoods::getCreateTime);  // ?
//
//                // 
//                Page<OGoods> goodsPage = oGoodsService.page(page, queryWrapper);
//                List<OGoods> records = goodsPage.getRecords();
//
//                if (records.isEmpty()) {
//                    log.info("没有更多商品数据需要同");
//                    hasMore = false;
//                    break;
//                }
//
//                // 
//                for (OGoods goods : records) {
//                    try {
//                        log.info("开始同步商�? goodsId: {}", goods.getId());
//
//                        // 
//                        syncGoods(goods.getId(), null);
//
//                        log.info("商品同步成功, goodsId: {}", goods.getId());
//
//                        // 5�?
//                        Thread.sleep(5000);
//
//                    } catch (Exception e) {
//                        log.error("同步商品失败, goodsId: {}, error: {}", goods.getId(), e.getMessage());
//                        // ?
//                    }
//                }
//
//                // ?
//                hasMore = records.size() >= pageSize;
//                pageNum++;
//
//                log.info("�?{} 页数据处理完�?, pageNum - 1);
//            }
//
//            log.info("批量同步任务完成");
//
//        } catch (Exception e) {
//            log.error("批量同步任务执行失败: {}", e.getMessage(), e);
//            throw new RuntimeException("批量同步任务执行失败", e);
//        }
//    }

    @Override
    @Async
    public void asyncSheinGoodsStatus(List<String> goodsIdList) {
        for (String goodsId : goodsIdList) {
            try {
                this.updateSheinGoodsStatus(goodsId);
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void updateSheinGoodsStatus(String goodsId) {
        List<OmsSheinGoods> sheinGoodsList = sheinGoodsMapper.selectList(new LambdaQueryWrapper<>(OmsSheinGoods.class).eq(OmsSheinGoods::getOGoodsId, goodsId));
        if (CollectionUtils.isEmpty(sheinGoodsList)) {
            log.info("当前erp 商品 {} 获取不到关联的shein店铺商品",goodsId);
            return ;
        }
        OmsSheinGoods sheinGoods = sheinGoodsList.get(0);
        DocumentStateRequest documentStateRequest = new DocumentStateRequest();
        List<DocumentStateRequest.SpuInfo> spuList = new ArrayList<>();
        DocumentStateRequest.SpuInfo spuInfo = new DocumentStateRequest.SpuInfo();
        spuInfo.setSpuName(sheinGoods.getSpuName());
        spuList.add(spuInfo);
        documentStateRequest.setSpuList(spuList);
        OShopPlatform platform = platformService.selectById(EnumShopType.SHEIN.getIndex());
        String appKey = platform.getAppKey();
        String appSecret = platform.getAppSecret();
        String serverUrl = platform.getServerUrl();
        documentStateRequest.setUrl(serverUrl);
        documentStateRequest.setAppKey(appKey);
        documentStateRequest.setAppSecret(appSecret);
        Integer documentState = null;
        try {
            DocumentStateResponse documentStateResponse = SheinApiHelper.queryDocumentState(documentStateRequest);
            if (documentStateResponse.getCode().equals("0")) {
                DocumentStateResponse.Info info = documentStateResponse.getInfo();
                List<DocumentStateResponse.SpuStateInfo> spuStateInfos = info.getData();
                if (CollectionUtils.isNotEmpty(spuStateInfos)){
                    DocumentStateResponse.SpuStateInfo spuStateInfo = spuStateInfos.get(0);
                    List<DocumentStateResponse.SkcStateInfo> skcList = spuStateInfo.getSkcList();
                    if (CollectionUtils.isNotEmpty(skcList)){
                        DocumentStateResponse.SkcStateInfo skcStateInfo = skcList.get(0);
                         documentState = skcStateInfo.getDocumentState();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (documentState ==null){
            return;
        }
        sheinGoods.setCheckStatus(documentState);
        sheinGoodsMapper.updateById(sheinGoods);
    }

    @Override
    public Map<Long, Integer> selectSheinCheckStatus(List<String> goodsIds) {
        List<OmsSheinGoods> sheinGoodsList = sheinGoodsMapper.selectList(new LambdaQueryWrapper<>(OmsSheinGoods.class).in(OmsSheinGoods::getOGoodsId, goodsIds));
        if (CollectionUtils.isEmpty(sheinGoodsList)) {
            return Map.of();
        }
        Map<Long, List<OmsSheinGoods>> sheinGoodsGroupByOGoodsId = sheinGoodsList.stream().collect(Collectors.groupingBy(OmsSheinGoods::getOGoodsId, Collectors.toList()));
        Map<Long, Integer> map = new HashMap<>();
        sheinGoodsGroupByOGoodsId.forEach((goodsId,sheinGoods)->{
            map.put(goodsId,sheinGoods.get(0).getCheckStatus());
        });
        applicationContext.getBean(SheinGoodsCommonService.class).asyncSheinGoodsStatus(goodsIds);
        return map;
    }

}
