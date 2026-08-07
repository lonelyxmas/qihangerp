package cn.qihangerp.oms.stock.controller;

import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.module.wms.domain.OWarehouse;
import cn.qihangerp.module.wms.domain.OWarehousePosition;
import cn.qihangerp.oms.service.OWarehousePositionService;
import cn.qihangerp.oms.service.OWarehouseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/oms-api/warehouse")
public class WarehouseController extends BaseController {
    private final OWarehouseService warehouseService;
    private final OWarehousePositionService positionService;
    @GetMapping("/list")
    public TableDataInfo list(OWarehouse bo)
    {
        LambdaQueryWrapper<OWarehouse> qw = new LambdaQueryWrapper<OWarehouse>()
                .eq(bo.getStatus()!=null, OWarehouse::getStatus, bo.getStatus())
                .like(StringUtils.hasText(bo.getNumber()), OWarehouse::getNumber,bo.getNumber())
                .like(StringUtils.hasText(bo.getName()), OWarehouse::getName,bo.getName())
                ;
        List<OWarehouse> oWarehouses = warehouseService.list(qw);
        return getDataTable(oWarehouses);
    }

    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(warehouseService.getById(id));
    }
    @PostMapping
    public AjaxResult add(@RequestBody OWarehouse warehouse)
    {
        warehouse.setCreateBy(getUsername());
        warehouse.setCreateTime(new Date());
        boolean save = warehouseService.save(warehouse);
        if(save){
            OWarehousePosition position = new OWarehousePosition();
            position.setWarehouseId(warehouse.getId());
            position.setParentId(0);
            position.setParentId1(0);
            position.setParentId2(0);
            position.setNumber(warehouse.getNumber());
            position.setName(warehouse.getName());
            position.setIsDelete(0);
            position.setAddress(warehouse.getAddress());
            position.setRemark(warehouse.getRemark());
            position.setCreateBy(getUsername());
            position.setCreateTime(new Date());
            positionService.save(position);
        }
        return AjaxResult.success();
    }
    @PutMapping
    public AjaxResult edit(@RequestBody OWarehouse warehouse)
    {
        warehouse.setUpdateBy(getUsername());
        warehouse.setUpdateTime(new Date());
        return toAjax(warehouseService.updateById(warehouse));
    }
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(warehouseService.removeBatchByIds(Arrays.stream(ids).toList()));
    }

    @GetMapping("/position/list")
    public TableDataInfo positionList(Long warehouseId)
    {
        LambdaQueryWrapper<OWarehousePosition> qw = new LambdaQueryWrapper<OWarehousePosition>()
                .eq(OWarehousePosition::getWarehouseId,warehouseId)
                ;
        List<OWarehousePosition> list = positionService.list(qw);
        return getDataTable(list);
    }
    @GetMapping("/position/search")
    public TableDataInfo searchPosition(Long warehouseId,String number)
    {
        LambdaQueryWrapper<OWarehousePosition> qw = new LambdaQueryWrapper<OWarehousePosition>()
                .eq(OWarehousePosition::getWarehouseId,warehouseId)
                .like(OWarehousePosition::getNumber,number)
                ;
        List<OWarehousePosition> list = positionService.list(qw);
        return getDataTable(list);
    }


    @PostMapping("/position")
    public AjaxResult positionAdd(@RequestBody OWarehousePosition position) {
        position.setCreateBy(getUsername());
        position.setCreateTime(new Date());
        position.setParentId1(0);
        position.setParentId2(0);
        positionService.save(position);

        return AjaxResult.success();
    }

    @GetMapping(value = "/position/{id}")
    public AjaxResult getPositionInfo(@PathVariable("id") Long id)
    {
        return success(positionService.getById(id));
    }

    @PutMapping("/position")
    public AjaxResult positionEdit(@RequestBody OWarehousePosition position)
    {
        position.setUpdateBy(getUsername());
        position.setUpdateTime(new Date());
        return toAjax(positionService.updateById(position));
    }
    @DeleteMapping("/position/{ids}")
    public AjaxResult positionRemove(@PathVariable Long[] ids)
    {
        return toAjax(positionService.removeBatchByIds(Arrays.stream(ids).toList()));
    }


}
