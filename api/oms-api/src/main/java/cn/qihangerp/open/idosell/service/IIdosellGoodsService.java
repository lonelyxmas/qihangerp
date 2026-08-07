package cn.qihangerp.open.idosell.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.module.open.idosell.domain.bo.IdosellGoodsBo;
import cn.qihangerp.module.open.idosell.domain.vo.IdosellGoods;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * IdoSell商品信息表 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2025-02-08
 */
public interface IIdosellGoodsService extends IService<IdosellGoods> {

    PageResult<IdosellGoods> queryPageList(IdosellGoodsBo bo, PageQuery pageQuery);

    void importGoods(Long shopId);

    ResultVo sync(IdosellGoodsBo bo);

    void batchSync();
}
