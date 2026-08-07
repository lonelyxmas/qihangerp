package cn.qihangerp.oms.task;

import cn.qihangerp.common.config.RedisCache;
import cn.qihangerp.common.task.IPollableService;
import cn.qihangerp.domain.SysTask;
import cn.qihangerp.oms.service.OOrderService;
import cn.qihangerp.oms.service.SysTaskService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Log
@AllArgsConstructor
@Service
public class IdosellOrderPullTask implements IPollableService {
    private final SysTaskService taskService;

    private final OOrderService orderService;
    private final RedisCache redisCache;

    @Override
    public void poll() {
        log.info("=======自动任务==Idosell订单拉取=========" + LocalDateTime.now());

    }

    @Override
    public String getCronExpression() {
        SysTask task = taskService.getById(2001);
        if(task!=null&&task.getStatus().intValue() == 1) {
            return task.getCron();
        }else return "-";
    }
}