package cn.qihangerp.open.idosell.service;

import cn.qihangerp.common.ResultVo;

import java.io.IOException;

public interface IdosellGoodsCommonService {
    ResultVo publishToIdosell(Long goodsId, Long shopId) throws IOException;
}
