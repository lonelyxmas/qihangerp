package cn.qihangerp.oms.service.impl;


import cn.qihangerp.domain.SysTask;

import cn.qihangerp.module.mapper.SysTaskMapper;
import cn.qihangerp.oms.service.SysTaskService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author TW
* @description 针对表【sys_task】的数据库操作Service实现
* @createDate 2024-03-22 19:34:41
*/
@AllArgsConstructor
@Service
public class SysTaskServiceImpl extends ServiceImpl<SysTaskMapper, SysTask>
    implements SysTaskService {
    private final SysTaskMapper mapper;

    @Override
    public PageResult<SysTask> queryPageList(PageQuery pageQuery) {
        LambdaQueryWrapper<SysTask> queryWrapper = new LambdaQueryWrapper<SysTask>().eq(SysTask::getStatus,1);
        Page<SysTask> pages = mapper.selectPage(pageQuery.build(), queryWrapper);
        return PageResult.build(pages);
    }
}




