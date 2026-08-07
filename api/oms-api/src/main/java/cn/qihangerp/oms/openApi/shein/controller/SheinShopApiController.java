package cn.qihangerp.oms.openApi.shein.controller;

import cn.qihangerp.oms.openApi.PullRequest;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.domain.OShopPlatform;
import cn.qihangerp.module.open.shein.domain.OmsSheinProductTypeAttr;
import cn.qihangerp.module.open.shein.domain.OmsSheinProductAttrVal;
import cn.qihangerp.module.open.shein.domain.OmsSheinShopCategory;
import cn.qihangerp.open.shein.helper.SheinProductTypeAttrApiHelper;
import cn.qihangerp.open.shein.helper.SheinShopApiHelper;
import cn.qihangerp.open.shein.helper.model.AttributeTemplateResponse;
import cn.qihangerp.open.shein.helper.model.CategoryResponse;
import cn.qihangerp.open.shein.request.CategoryRequest;
import cn.qihangerp.open.shein.service.OmsSheinProductTypeAttrService;
import cn.qihangerp.open.shein.service.OmsSheinShopCategoryService;
import cn.qihangerp.oms.service.OShopPlatformService;
import cn.qihangerp.oms.service.OShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping("/api/open-api/shein/shop")
@RestController
@RequiredArgsConstructor
public class SheinShopApiController extends BaseController {

    private final OShopService shopService;
    private final OmsSheinShopCategoryService shopCategoryService;
    private final OShopPlatformService platformService;
    private final OmsSheinProductTypeAttrService productTypeAttrService;
    /**
     * 拉取分类列表
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pull_category", method = RequestMethod.POST)
    public AjaxResult pullCategory(@RequestBody(required = false) PullRequest request) throws Exception {
        if(request.getShopId() == null||request.getShopId()<=0) return AjaxResult.error("缺少参数：shopId");

//        OShop shop = shopService.getById(request.getShopId());
//        if(shop == null)return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:shop not exist");
//        else if(!StringUtils.hasText(shop.getApiRequestUrl())) return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:missing api request url");
//        else if(!StringUtils.hasText(shop.getAppKey())) return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:missing api key");
        OShopPlatform platform = platformService.selectById(EnumShopType.SHEIN.getIndex());
        CategoryResponse response = SheinShopApiHelper.queryCategoryTree(platform.getServerUrl(),platform.getAppKey(),platform.getAppSecret());
        if(response==null) return AjaxResult.error("接口拉取错误");
        if(!response.getCode().equals("0")) return AjaxResult.error(response.getMsg());
        //循环插入订单数据到数据库
        for (var category : response.getInfo().getData()) {
            OmsSheinShopCategory shopCategory = new OmsSheinShopCategory();
           shopCategory.setCategoryId(category.getCategoryId());
           shopCategory.setCategoryName(category.getCategoryName());
           shopCategory.setLastCategory(category.getLastCategory().toString());
           shopCategory.setParentCategoryId(category.getParentCategoryId());
           shopCategory.setProductTypeId(category.getProductTypeId());
           shopCategory.setShopId(request.getShopId());
           shopCategoryService.saveCategory(shopCategory);
           if(category.getChildren()!=null&&category.getChildren().size()>0){
               for (var child : category.getChildren()) {
                   OmsSheinShopCategory shopCategoryChild = new OmsSheinShopCategory();
                   shopCategoryChild.setCategoryId(child.getCategoryId());
                   shopCategoryChild.setCategoryName(child.getCategoryName());
                   shopCategoryChild.setLastCategory(child.getLastCategory().toString());
                   shopCategoryChild.setParentCategoryId(child.getParentCategoryId());
                   shopCategoryChild.setProductTypeId(child.getProductTypeId());
                   shopCategoryChild.setShopId(request.getShopId());
                   shopCategoryService.saveCategory(shopCategoryChild);
                   if(child.getChildren()!=null&&child.getChildren().size()>0){
                       for (var childChild : child.getChildren()) {
                           OmsSheinShopCategory shopCategoryChild2 = new OmsSheinShopCategory();
                           shopCategoryChild2.setCategoryId(childChild.getCategoryId());
                           shopCategoryChild2.setCategoryName(childChild.getCategoryName());
                           shopCategoryChild2.setLastCategory(childChild.getLastCategory().toString());
                           shopCategoryChild2.setParentCategoryId(childChild.getParentCategoryId());
                           shopCategoryChild2.setProductTypeId(childChild.getProductTypeId());
                           shopCategoryChild2.setShopId(request.getShopId());
                           shopCategoryService.saveCategory(shopCategoryChild2);
                           if(childChild.getChildren()!=null&&childChild.getChildren().size()>0){
                               for (var childChildChild : childChild.getChildren()) {
                                   OmsSheinShopCategory shopCategoryChild3 = new OmsSheinShopCategory();
                                   shopCategoryChild3.setCategoryId(childChildChild.getCategoryId());
                                   shopCategoryChild3.setCategoryName(childChildChild.getCategoryName());
                                   shopCategoryChild3.setLastCategory(childChildChild.getLastCategory().toString());
                                   shopCategoryChild3.setParentCategoryId(childChildChild.getParentCategoryId());
                                   shopCategoryChild3.setProductTypeId(childChildChild.getProductTypeId());
                                   shopCategoryChild3.setShopId(request.getShopId());
                                   shopCategoryService.saveCategory(shopCategoryChild3);
                               }
                           }
                       }
                   }
               }
           }

        }

        return AjaxResult.success("SUCCESS");
    }
    @RequestMapping(value = "/pull_product_type_attr", method = RequestMethod.POST)
    public AjaxResult pullProductTypeAttr(@RequestBody CategoryRequest request) throws Exception {
        if(request.getProductTypeId()==null||request.getProductTypeId()<=0) return AjaxResult.error("Param Error");
        OShopPlatform platform = platformService.selectById(EnumShopType.SHEIN.getIndex());
        if(!StringUtils.hasText(platform.getServerUrl())) return AjaxResult.error("Platform Param Error");
        if(!StringUtils.hasText(platform.getAppKey())) return AjaxResult.error("Platform Param Error");
        if(!StringUtils.hasText(platform.getAppSecret())) return AjaxResult.error("Platform Param Error");
//        Long productTypeId = 1691L;
        AttributeTemplateResponse response = SheinProductTypeAttrApiHelper.queryAttributeTemplate(platform.getServerUrl(), platform.getAppKey(), platform.getAppSecret(), Collections.singletonList(request.getProductTypeId()));
        if(response==null) return AjaxResult.error("接口拉取错误");
        if(!response.getCode().equals("0")) return AjaxResult.error(response.getMsg());
        // 处理数据
        saveAttr(response);

        return AjaxResult.success("SUCCESS");
    }
    private void saveAttr(AttributeTemplateResponse response) {
        for (var result : response.getInfo().getData()) {

            if (result.getAttributeInfos() != null && result.getAttributeInfos().size() > 0) {
                for (var attrAttr : result.getAttributeInfos()) {
                    OmsSheinProductTypeAttr attr = new OmsSheinProductTypeAttr();
                    attr.setProductTypeId(result.getProductTypeId());
                    attr.setAttributeId(attrAttr.getAttributeId());
                    attr.setAttributeName(attrAttr.getAttributeName());
                    attr.setAttributeType(attrAttr.getAttributeType());
                    attr.setAttributeLabel(attrAttr.getAttributeLabel());
                    attr.setAttributeStatus(attrAttr.getAttributeStatus());
                    attr.setAttributeMode(attrAttr.getAttributeMode());
                    attr.setAttributeIsShow(attrAttr.getAttributeIsShow());
                    List<OmsSheinProductAttrVal> valList = new ArrayList<>();
                    if (attrAttr.getAttributeValueInfoList() != null && attrAttr.getAttributeValueInfoList().size() > 0) {
                        for (var attrValueInfo : attrAttr.getAttributeValueInfoList()) {
                            OmsSheinProductAttrVal val = new OmsSheinProductAttrVal();
                            val.setAttributeId(attr.getAttributeId());
                            val.setAttributeValueId(attrValueInfo.getAttributeValueId());
                            val.setAttributeValue(attrValueInfo.getAttributeValue());
                            val.setIsShow(attrValueInfo.getIsShow());
                            val.setIsCustomAttributeValue(attrValueInfo.getIsCustomAttributeValue().toString());
                            val.setSupplierId(attrValueInfo.getSupplierId());
                            valList.add(val);
                        }
                    }
                    attr.setVals(valList);
                    productTypeAttrService.saveAttr(attr);
                }
            }
        }
    }

    @RequestMapping(value = "/pull_product_type_attr_all", method = RequestMethod.POST)
    public AjaxResult pullProductTypeAttr() throws IOException {
        OShopPlatform platform = platformService.selectById(EnumShopType.SHEIN.getIndex());
        List<OmsSheinShopCategory> list = shopCategoryService.list();
        Set<Long> uniqueProductTypeIds = list.stream()
                .map(OmsSheinShopCategory::getProductTypeId)
                .filter(productTypeId -> productTypeId != null && productTypeId > 0)
                .collect(Collectors.toSet());

        List<Long> productTypeIdList = uniqueProductTypeIds.stream().collect(Collectors.toList());
        for(Long productId : productTypeIdList){
            AttributeTemplateResponse response = SheinProductTypeAttrApiHelper.queryAttributeTemplate(platform.getServerUrl(), platform.getAppKey(), platform.getAppSecret(), Collections.singletonList(productId));
            if(response==null) return AjaxResult.error("接口拉取错误");
            if(!response.getCode().equals("0")) return AjaxResult.error(response.getMsg());
            // 处理数据
            saveAttr(response);
        }

        return AjaxResult.success("SUCCESS");
    }
    @RequestMapping(value = "/push_product_type_attr_oms", method = RequestMethod.POST)
    public AjaxResult pushProductTypeAttrOms() throws IOException {
        shopCategoryService.batchPushToOms();
        return AjaxResult.success("SUCCESS");
    }

}
