package com.hughswb.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hughswb.blog.dto.OperationLogDTO;
import com.hughswb.blog.vo.PageResult;
import com.hughswb.blog.entity.OperationLog;
import com.hughswb.blog.vo.ConditionVO;

/**
 * 操作日志服务
 *
 * @author swb
 * @date 2021/07/29
 */
public interface OperationLogService extends IService<OperationLog> {

    /**
     * 查询日志列表
     *
     * @param conditionVO 条件
     * @return 日志列表
     */
    PageResult<OperationLogDTO> listOperationLogs(ConditionVO conditionVO);

}
