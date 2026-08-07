package cn.qihangerp.oms.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.module.goods.domain.OGoodsInventoryRecord;
import cn.qihangerp.module.goods.domain.bo.OGoodsInventoryOperateBo;
import cn.qihangerp.module.goods.mapper.OGoodsInventoryRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.goods.domain.OGoodsInventory;
import cn.qihangerp.oms.service.OGoodsInventoryService;
import cn.qihangerp.module.goods.mapper.OGoodsInventoryMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
* @author qilip
* @description 针对表【o_goods_inventory(商品库存表)】的数据库操作Service实现
* @createDate 2024-09-23 22:39:50
*/
@AllArgsConstructor
@Service
public class OGoodsInventoryServiceImpl extends ServiceImpl<OGoodsInventoryMapper, OGoodsInventory>
    implements OGoodsInventoryService{
    private final OGoodsInventoryMapper mapper;
    private final OGoodsInventoryRecordMapper oGoodsInventoryRecordMapper;
    @Override
    public PageResult<OGoodsInventory> queryPageList(OGoodsInventory bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OGoodsInventory> queryWrapper = new LambdaQueryWrapper<OGoodsInventory>();
        queryWrapper.eq(bo.getGoodsId()!=null,OGoodsInventory::getGoodsId,bo.getGoodsId());
        queryWrapper.eq(bo.getSkuId()!=null,OGoodsInventory::getSkuId,bo.getSkuId());
        queryWrapper.eq(StringUtils.hasText(bo.getGoodsNum()),OGoodsInventory::getGoodsNum,bo.getGoodsNum());
        queryWrapper.eq(StringUtils.hasText(bo.getSkuCode()),OGoodsInventory::getSkuCode,bo.getSkuCode());

        Page<OGoodsInventory> pages = mapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(pages);
    }

    @Override
    public long getAllInventoryQuantity() {
        return mapper.getAllInventoryQuantity();
    }

    @Transactional
    @Override
    public ResultVo updateInventory(OGoodsInventoryOperateBo bo) {
        if(bo.getId()==null){
            return ResultVo.error("Param Error");
        }
        if(bo.getType()==null){
            return ResultVo.error("Param Error");
        }
        if(bo.getQuantity()==null){
            return ResultVo.error("Param Error");
        } else if (bo.getQuantity()==0) {
            return ResultVo.error("Param Error");
        }
        OGoodsInventory inventory = mapper.selectById(bo.getId());
        if(inventory==null){
            return ResultVo.error("Not Found Inventory");
        }
        if(bo.getQuantity()>inventory.getQuantity()){
            return ResultVo.error("Quantity Error");
        }
        OGoodsInventory update =new OGoodsInventory();
        update.setId(bo.getId());
        if(bo.getType().intValue()==2) {
            update.setQuantity(inventory.getQuantity() - bo.getQuantity());
            update.setUpdateBy("手动扣减库存");
        }else if (bo.getType().intValue()==1) {
            update.setQuantity(inventory.getQuantity() + bo.getQuantity());
            update.setUpdateBy("手动增加库存");
        }
        update.setUpdateTime(new Date());
        mapper.updateById(update);

        // 增加库存入库记录
        OGoodsInventoryRecord inventoryRecord = new OGoodsInventoryRecord();
        inventoryRecord.setInventoryId(inventory.getId());
        inventoryRecord.setGoodsId(inventory.getGoodsId());
        inventoryRecord.setGoodsNum(inventory.getGoodsNum());
        inventoryRecord.setSkuId(inventory.getSkuId());
        inventoryRecord.setSkuCode(inventory.getSkuCode());
        inventoryRecord.setBatchId(0L);
        inventoryRecord.setType(bo.getType());
        inventoryRecord.setInventoryDetailId(0L);
        inventoryRecord.setQuantity(bo.getQuantity());
        inventoryRecord.setBalanceQuantity(update.getQuantity());
        inventoryRecord.setLockedQuantity(0);
        inventoryRecord.setBizType(0);
        inventoryRecord.setBizId(0L);
        inventoryRecord.setBizItemId(0L);
        inventoryRecord.setStatus(1);
        inventoryRecord.setRemark(bo.getRemark());
        inventoryRecord.setWarehouseId(0L);
        inventoryRecord.setPositionId(0L);
        inventoryRecord.setCreateBy("手动操作库存");
        oGoodsInventoryRecordMapper.insert(inventoryRecord);
        return ResultVo.success();
    }
}




