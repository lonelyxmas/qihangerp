package cn.qihangerp.oms.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;

import cn.qihangerp.module.wms.domain.OGoodsStockIn;
import cn.qihangerp.module.wms.request.StockInCreateRequest;
import cn.qihangerp.module.wms.request.StockInRequest;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【wms_stock_in(入库单)】的数据库操作Service
* @createDate 2024-09-22 16:10:08
*/
public interface OGoodsStockInService extends IService<OGoodsStockIn> {
    PageResult<OGoodsStockIn> queryPageList(OGoodsStockIn bo, PageQuery pageQuery);
    ResultVo<Long> createEntry(Long userId, String userName, StockInCreateRequest request);
    ResultVo<Long> stockIn(Long userId, String userName, StockInRequest request);

    OGoodsStockIn getDetailAndItemById(Long id);
}
