package cn.qihangerp.oms.openApi.idosell.controller;

import cn.qihangerp.oms.openApi.BindOmsCategoryRequest;
import cn.qihangerp.oms.openApi.PullRequest;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.enums.HttpStatus;
import cn.qihangerp.domain.OShop;
import cn.qihangerp.domain.OShopPlatform;
import cn.qihangerp.open.common.ApiResultVo;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellSaleAttrVal;
import cn.qihangerp.open.idosell.helper.IdosellShopApiHelper;
import cn.qihangerp.open.idosell.response.ShopCategory;
import cn.qihangerp.module.open.idosell.domain.OmsIdosellShopCategory;
import cn.qihangerp.open.idosell.response.SizeAttributeResponse;
import cn.qihangerp.open.idosell.service.OmsIdosellSaleAttrValService;
import cn.qihangerp.open.idosell.service.OmsIdosellShopCategoryService;
import cn.qihangerp.oms.service.OShopPlatformService;
import cn.qihangerp.oms.service.OShopService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/api/open-api/idosell/shop")
@RestController
@RequiredArgsConstructor
public class IdosellShopApiController extends BaseController {
    private final IdosellShopApiHelper shopApiHelper;
    private final OShopService shopService;
    private final OmsIdosellShopCategoryService shopCategoryService;
    private final OShopPlatformService platformService;
    private final OmsIdosellSaleAttrValService saleAttrValService;
    /**
     * 拉取商品列表（包含sku）
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pull_category", method = RequestMethod.POST)
    public AjaxResult pullCategory(@RequestBody(required = false) PullRequest request) throws Exception {
        if(request.getShopId() == null||request.getShopId()<=0) return AjaxResult.error("缺少参数：shopId");

        OShop shop = shopService.getById(request.getShopId());
        if(shop == null)return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:shop not exist");
        else if(!StringUtils.hasText(shop.getApiRequestUrl())) return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:missing api request url");
        else if(!StringUtils.hasText(shop.getAppKey())) return AjaxResult.error(HttpStatus.PARAMS_ERROR, "Error:missing api key");

        ApiResultVo<ShopCategory> apiResultVo = shopApiHelper.getShopCategory(shop.getApiRequestUrl(), shop.getAppKey());
        if(apiResultVo.getCode()!=0) return AjaxResult.error(apiResultVo.getCode(),apiResultVo.getMsg());
        //循环插入订单数据到数据库
        for (var category : apiResultVo.getList()) {
            OmsIdosellShopCategory shopCategory = new OmsIdosellShopCategory();
            BeanUtils.copyProperties(category, shopCategory);
            shopCategory.setLangData(JSONObject.toJSONString(category.getLangData()));
            shopCategory.setShopId(request.getShopId());
            var en = category.getLangData().stream().filter(x -> x.getLangId().equals("eng"))
                    .findFirst()
                    .orElseGet(() -> category.getLangData().isEmpty() ? null : category.getLangData().get(0));
            if(en != null) {
                shopCategory.setName(en.getPluralName());
            }
            shopCategoryService.saveCategory(shopCategory);

        }

        return AjaxResult.success("SUCCESS");
    }
    @RequestMapping(value = "/bind_oms_category", method = RequestMethod.POST)
    public AjaxResult bindOmsCategory(@RequestBody BindOmsCategoryRequest request) throws Exception {
        if (request.getId() == null || request.getId() <= 0) return AjaxResult.error("缺少参数：id");
        if (request.getCategoryId() == null || request.getCategoryId() <= 0)
            return AjaxResult.error("缺少参数：CategoryId");
        ResultVo resultVo = shopCategoryService.bindOmsCategory(request.getId().intValue(), request.getCategoryId());
        if (resultVo.getCode() == 0) return AjaxResult.success();
        else return AjaxResult.error(resultVo.getMsg());
    }

    @RequestMapping(value = "/pull_size_attr", method = RequestMethod.POST)
    public AjaxResult pullSizeAttr() throws Exception {
        OShopPlatform platform = platformService.selectById(EnumShopType.IDOSELL.getIndex());
        if(!StringUtils.hasText(platform.getServerUrl())) return AjaxResult.error("Platform Param Error");
        if(!StringUtils.hasText(platform.getAppKey())) return AjaxResult.error("Platform Param Error");


        SizeAttributeResponse response = shopApiHelper.getSizeAttr(platform.getServerUrl(), platform.getAppKey());
        if(response==null) return AjaxResult.error("数据获取错误");
        if(response.getSizeGroups()==null||response.getSizeGroups().isEmpty()) return AjaxResult.error("没有数据");

        for(var sizeGroup : response.getSizeGroups()) {
            if(sizeGroup.getSizes()!=null&&!sizeGroup.getSizes().isEmpty()) {
                for (var size : sizeGroup.getSizes()) {
                    // 添加Size到数据库
                    List<OmsIdosellSaleAttrVal> sizeValList = saleAttrValService.list(
                            new LambdaQueryWrapper<OmsIdosellSaleAttrVal>()
                                    .eq(OmsIdosellSaleAttrVal::getAttributeValue, size.getSizeId())
                                    .eq(OmsIdosellSaleAttrVal::getAttributeId, 200)
                    );
                    OmsIdosellSaleAttrVal attrVal = new OmsIdosellSaleAttrVal();
                    attrVal.setAttributeId(200L);//属性id是固定的
                    attrVal.setAttributeValue(size.getSizeId());
                    attrVal.setSizePanelName(size.getSizeName());
                    attrVal.setLangData(JSONObject.toJSONString(size.getLangData()));
                    attrVal.setGroupId(sizeGroup.getGroupId());
                    attrVal.setGroupName(sizeGroup.getGroupName());
                    if (sizeValList == null || sizeValList.isEmpty()) {

                        saleAttrValService.save(attrVal);
                        log.info("新增size属性值{}", JSONObject.toJSONString(attrVal));
                    }else{
                        attrVal.setAttributeValueId(sizeValList.get(0).getAttributeValueId());
                        saleAttrValService.updateById(attrVal);
                        log.info("=======Size已存在,更新=========");
                    }
                }
            }
        }
        log.info("=====Size属性拉取完成======");

//        AttributeTemplateResponse response = SheinProductTypeAttrApiHelper.queryAttributeTemplate(platform.getServerUrl(), platform.getAppKey(), platform.getAppSecret(), Collections.singletonList(request.getProductTypeId()));
//        if(response==null) return AjaxResult.error("接口拉取错误");
//        if(!response.getCode().equals("0")) return AjaxResult.error(response.getMsg());
//        // 处理数据
//        saveAttr(response);

        return AjaxResult.success("SUCCESS");
    }
}
