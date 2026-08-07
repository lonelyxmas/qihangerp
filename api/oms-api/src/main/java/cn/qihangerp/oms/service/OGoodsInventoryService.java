package cn.qihangerp.oms.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.module.goods.domain.OGoodsInventory;
import cn.qihangerp.module.goods.domain.bo.OGoodsInventoryOperateBo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【o_goods_inventory(商品库存表)】的数据库操作Service
* @createDate 2024-09-23 22:39:50
*/
public interface OGoodsInventoryService extends IService<OGoodsInventory> {
    PageResult<OGoodsInventory> queryPageList(OGoodsInventory bo, PageQuery pageQuery);
    long getAllInventoryQuantity();
    ResultVo updateInventory(OGoodsInventoryOperateBo bo);
}
