package cn.qihangerp.open.idosell.service.impl;

import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.domain.OShop;
import cn.qihangerp.domain.OShopPlatform;
import cn.qihangerp.module.goods.domain.OGoods;
import cn.qihangerp.module.goods.domain.OGoodsSku;
import cn.qihangerp.oms.service.OGoodsService;
import cn.qihangerp.oms.service.OGoodsSkuService;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellSaleAttrVal;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellShopCategory;
import cn.qihangerp.module.open.idosell.domain.vo.IdosellGoods;
import cn.qihangerp.open.idosell.helper.IdosellApiHelper;
import cn.qihangerp.open.idosell.helper.IdosellGoodsApiHelper;
import cn.qihangerp.open.idosell.helper.request.PublishProductRequest;
import cn.qihangerp.open.idosell.helper.response.PublishProductResponse;
import cn.qihangerp.module.open.idosell.mapper.IdosellGoodsMapper;
import cn.qihangerp.module.open.idosell.mapper.OmsIdosellShopCategoryMapper;
import cn.qihangerp.open.idosell.service.IdosellGoodsCommonService;
import cn.qihangerp.open.idosell.service.OmsIdosellSaleAttrValService;
import cn.qihangerp.oms.service.OShopPlatformService;
import cn.qihangerp.oms.service.OShopService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class IdosellGoodsCommonServiceImpl implements IdosellGoodsCommonService {
    private final IdosellGoodsMapper idosellGoodsMapper;
    private final OShopPlatformService platformService;
    private final OGoodsService oGoodsService;
    private final OGoodsSkuService oGoodsSkuService;
    private final IdosellGoodsApiHelper idosellGoodsApiHelper;
    private final IdosellApiHelper idosellApiHelper;
    private final OmsIdosellSaleAttrValService osIdosellSaleAttrValService;
    private final OmsIdosellShopCategoryMapper shopCategoryMapper;
    private final OShopService shopService;

    @Override
    public ResultVo publishToIdosell(Long goodsId, Long shopId) throws IOException {
        List<IdosellGoods> localSheinGoodsList = idosellGoodsMapper.selectList(
                new LambdaQueryWrapper<>(IdosellGoods.class).eq(IdosellGoods::getOGoodsId, goodsId));

        if (CollectionUtils.isNotEmpty(localSheinGoodsList)) {
            log.info("已经同步过了，则执行更新操作");
            return ResultVo.error("已经同步过了");
        }
        OShop shop = shopService.getById(shopId);
        if (shop == null) {
            log.info("没有找到Idosell店铺信息");
            return ResultVo.error("没有找到Idosell店铺信息");
        }
        log.info("=======开始发布商品到{}{}", shop.getId(), shop.getName());

        OShopPlatform platform = platformService.selectById(EnumShopType.IDOSELL.getIndex());
        if (platform == null) {
            log.info("没有找到Idosell平台配置信息");
            return ResultVo.error("没有找到Idosell平台配置信息");
        }
        OGoods oGoods = oGoodsService.selectGoodsById(Long.valueOf(goodsId));
        //        idosellGoodsApiHelper.publishProduct(platform.getServerUrl(), platform.getAppKey(),null);
        if (oGoods == null) {
            log.error("没有找到商品数据");
            return ResultVo.error("没有找到商品数据");
        }
        List<OGoodsSku> oGoodsSkuList = oGoodsSkuService.list(new LambdaQueryWrapper<OGoodsSku>().eq(OGoodsSku::getGoodsId, oGoods.getId()));
        if (oGoodsSkuList == null || oGoodsSkuList.isEmpty()) {
            log.error("没有找到商品SKU数据");
            return ResultVo.error("没有找到商品SKU数据");
        }
        // 获取 分类
        List<OmsIdosellShopCategory> shopCategories = shopCategoryMapper.selectList(
                new LambdaQueryWrapper<OmsIdosellShopCategory>()
                        .eq(OmsIdosellShopCategory::getOmsCategoryId, oGoods.getCategoryId()));
        if (shopCategories == null || shopCategories.isEmpty()) {
            log.error("没有找到对应的Idosell分类信息");
            return ResultVo.error("没有找到对应的Idosell分类信息");
        }
        // 拆分skc维度组合商品
        Map<Long, List<OGoodsSku>> skcList = oGoodsSkuList.stream().collect(Collectors.groupingBy(OGoodsSku::getColorValueId));

        // 创建 PublishProductRequest 对象
        PublishProductRequest request = new PublishProductRequest();

        // 创建产品列表
        List<PublishProductRequest.Product> products = new ArrayList<>();
//        skcList.forEach((colorValueId, skuList) -> {
        for (Map.Entry<Long, List<OGoodsSku>> entry : skcList.entrySet()) {
            Long colorValueId = entry.getKey();
            List<OGoodsSku> skuList = entry.getValue();
            // 找出idosell对应的属性值英文
            List<OmsIdosellSaleAttrVal> colorList = osIdosellSaleAttrValService.list(
                    new LambdaQueryWrapper<OmsIdosellSaleAttrVal>()
                            .eq(OmsIdosellSaleAttrVal::getOmsAttributeValueId, colorValueId));
            if (colorList == null || colorList.isEmpty()) {
                return ResultVo.error("没有找到Idosell颜色属性值：" + colorValueId);
            }
            String productDisplayedCode = oGoods.getGoodsNum();
            productDisplayedCode += " " + colorList.get(0).getAttributeValue();


            // 创建一个 Product 对象并设置其属性
            PublishProductRequest.Product product1 = new PublishProductRequest.Product();
//            product1.setProductId(2105);
//            product1.setProductSizeCodeExternal("0000021050190");
            product1.setProductDisplayedCode(productDisplayedCode);
            product1.setProductTaxCode("");
            product1.setProductInWrapper(1);
            product1.setProductSellByRetail(1f);
            product1.setProductSellByWholesale(1f);
//            product1.setCategoryIdoSellId(5975);
//            product1.setCategoryIdoSellPath("Ubrania i akcesoria > Buty > Damskie");
            product1.setCategoryId(shopCategories.get(0).getId());
            product1.setCategoryName(shopCategories.get(0).getName());
//            product1.setProducerId(1568641976);
//            product1.setProducerName("Gemre");
            product1.setCnTaricCode("");
            product1.setCountryOfOrigin("");
            product1.setUnitId(1);
            product1.setSeriesId(0);
            product1.setSeriesPanelName("");
            product1.setSizesGroupId(2);
            product1.setPriceChangeMode("amount_set");
//            product1.setShopsMask(-1);
            // 价格信息
            BigDecimal retailPrice = skuList.get(0).getRetailPrice();
            BigDecimal wholePrice = skuList.get(0).getWholePrice();
            // 创建并设置价格公式
            PublishProductRequest.Product.PriceFormula priceFormula = new PublishProductRequest.Product.PriceFormula();
            priceFormula.setProductRetailPrice(retailPrice.floatValue());
            priceFormula.setProductWholesalePrice(wholePrice.floatValue());
            priceFormula.setProductMinimalPrice(wholePrice.floatValue());
            priceFormula.setProductAutomaticCalculationPrice(0f);
            priceFormula.setProductPosPrice(0f);
            priceFormula.setProductVat(23f);
            priceFormula.setProductVatFree("n");
            product1.setPriceFormula(priceFormula);

            product1.setProductEnableInPos("n");
            product1.setProductAdvancePrice(0f);
            product1.setProductNote("");
            product1.setProductProfitPoints(0f);
            product1.setProductWeight(0);
            product1.setProductInVisible("n");
            product1.setProductInPersistent("y");
            product1.setShopsMask(4085);
            product1.setProductComplexNotes(0);
            product1.setProductInExportToPriceComparisonSites("y");
            product1.setProductInExportToAmazonMarketplace("n");
            product1.setAvailableProfile(1);
            product1.setProductRebate(0);
            product1.setWarrantyId(0);
            product1.setProductPriority(2);
            product1.setProductIcon(skuList.get(0).getColorImage());
//            product1.setProductIcon("https://gemre.com.pl/hpeciai/7dae323c9094f77bdd62404453a9601b/pol_is_2105-2105.jpg");
            product1.setProductWatermarkId(0);
            product1.setProductWatermarkUrl("");

            // 设置产品图片
            List<String> productPictures = new ArrayList<>();
            // 设置图片描述
            List<String> productDescriptionPictures = new ArrayList<>();
            for (var sku : skuList) {
                productPictures.add(sku.getColorImage());
                productDescriptionPictures.add("");
            }
//            productPictures.add("https://gemre.com.pl/hpeciai/02e21db9d7c6aaf7f01f029b7e674be5/pol_pl_2105-2105_1.jpg");
            product1.setProductPictures(productPictures);
            product1.setProductDescriptionPictures(productDescriptionPictures);

            // 创建并设置促销信息
            PublishProductRequest.Product.ProductPromotion productPromotion = new PublishProductRequest.Product.ProductPromotion();
            productPromotion.setPromoteInEnabled("n");
            productPromotion.setPromoteItemNormalPrice(0f);
            productPromotion.setPromoteItemWholesaleNormalPrice(0f);
            productPromotion.setPromoteItemEndingDate("0000-00-00");
            product1.setProductPromotion(productPromotion);

            // 创建并设置折扣信息
            PublishProductRequest.Product.ProductDiscount productDiscount = new PublishProductRequest.Product.ProductDiscount();
            productDiscount.setPromoteInEnabled("n");
            productDiscount.setPromoteItemNormalPrice(0f);
            productDiscount.setPromoteItemWholesaleNormalPrice(0f);
            productDiscount.setPromoteItemEndingDate("0000-00-00");
            product1.setProductDiscount(productDiscount);

            // 设置其他产品属性
            product1.setProductDistinguished(new PublishProductRequest.Product.ProductDistinguished());
            product1.setProductSpecial(new PublishProductRequest.Product.ProductSpecial());

            // skulist
            List<PublishProductRequest.ProductSize> productSizeList = new ArrayList<>();
            for (var sku : skuList) {
                List<OmsIdosellSaleAttrVal> sizeList = osIdosellSaleAttrValService.list(
                        new LambdaQueryWrapper<OmsIdosellSaleAttrVal>()
                                .eq(OmsIdosellSaleAttrVal::getOmsAttributeValueId, sku.getSizeValueId()));
                if (colorList == null || colorList.isEmpty()) {
                    log.error("没有找到Idosell Size属性值{}",sku.getSizeValueId());
                    return ResultVo.error("没有找到Idosell尺码属性值：" + sku.getSizeValueId());
                }

//                OmsIdosellSaleAttrVal sizeAttrValue = osIdosellSaleAttrValService.getById(sku.getSizeValueId());
//                if(sizeAttrValue==null){
//                    log.error("没有找到Idosell Size属性值{}",sku.getSizeValueId());
//                    return ResultVo.error("没有找到Idosell Size属性值"+sku.getSizeValueId());
//                }
                PublishProductRequest.ProductSize productSize = new PublishProductRequest.ProductSize();
                productSize.setSizeId(sizeList.get(0).getAttributeValueId().toString());
                productSize.setSizePanelName(sizeList.get(0).getAttributeValue());
                productSize.setProductWeight(0);
                productSize.setProductWeightNet(0);
                productSize.setProductRetailPrice(sku.getRetailPrice().floatValue());
                productSize.setProductWholesalePrice(sku.getWholePrice().floatValue());
                productSize.setProductMinimalPrice(sku.getRetailPrice().floatValue());
                productSize.setProductAutomaticCalculationPrice(0f);
                productSize.setProductPosPrice(0f);

                productSizeList.add(productSize);
            }
            product1.setProductSizes(productSizeList);

            // 将产品添加到产品列表
            products.add(product1);
        }


        PublishProductRequest.Params params = new PublishProductRequest.Params();
        params.setProducts(products);
        request.setParams(params);

        PublishProductResponse publishProductResponse = idosellApiHelper.publishProduct(platform.getServerUrl(), platform.getAppKey(), request);
        log.info("========添加Idosell产品响应: {}", publishProductResponse);
        return ResultVo.success();
    }
}
