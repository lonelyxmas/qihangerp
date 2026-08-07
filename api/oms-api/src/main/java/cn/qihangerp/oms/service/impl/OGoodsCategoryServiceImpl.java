package cn.qihangerp.oms.service.impl;

import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.module.goods.domain.OGoodsCategoryAttribute;
import cn.qihangerp.module.goods.mapper.OGoodsCategoryAttributeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.goods.domain.OGoodsCategory;
import cn.qihangerp.oms.service.OGoodsCategoryService;
import cn.qihangerp.module.goods.mapper.OGoodsCategoryMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
* @author qilip
* @description 针对表【o_goods_category】的数据库操作Service实现
* @createDate 2024-09-07 16:11:56
*/
@AllArgsConstructor
@Service
public class OGoodsCategoryServiceImpl extends ServiceImpl<OGoodsCategoryMapper, OGoodsCategory>
    implements OGoodsCategoryService{
    private final OGoodsCategoryMapper oGoodsCategoryMapper;
    private final OGoodsCategoryAttributeMapper attributeMapper;

    @Transactional
    @Override
    public void addCategory(OGoodsCategory category) {
        // 添加分类
        if(category.getSort()==null){
            category.setSort(0);
        }
        if(category.getParentId()==null){
            category.setParentId(0L);
        }
        category.setCreateTime(new Date());
        category.setIsDelete(0);
        oGoodsCategoryMapper.insert(category);
        // 如果是已经分类，添加默认规格
//        if(category.getParentId()==0) {
//            // 添加颜色规格
//            OGoodsCategoryAttribute att1 = new OGoodsCategoryAttribute();
//            att1.setCategoryId(category.getId());
//            att1.setType(1);
//            att1.setTitle("颜色");
//            att1.setCode("color");
//            attributeMapper.insert(att1);
//            // 添加颜色规格值
//            OGoodsCategoryAttributeValue av1 = new OGoodsCategoryAttributeValue();
//            av1.setCategoryAttributeId(att1.getId());
//            av1.setValue("默认");
//            av1.setSkuCode("00");
//            av1.setOrdernum(0);
//            av1.setIsdelete(0);
//            attributeValueMapper.insert(av1);
//
//        }
    }

    @Override
    public ResultVo<Long> delete(Long id) {
        List<OGoodsCategoryAttribute> attributes = attributeMapper.selectList(new LambdaQueryWrapper<OGoodsCategoryAttribute>().eq(OGoodsCategoryAttribute::getCategoryId, id));
        if(attributes!=null &&attributes.size()>0){
            return ResultVo.error( ResultVoEnum.DataError,"存在分类属性，请先删除分类属性");
        }else {
            oGoodsCategoryMapper.deleteById(id);
            return ResultVo.success();
        }
    }

    @Override
    public List<OGoodsCategory> listAndRelation() {
        return oGoodsCategoryMapper.listAndRelation();
    }
}




