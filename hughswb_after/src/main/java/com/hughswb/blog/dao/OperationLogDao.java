package com.hughswb.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hughswb.blog.entity.OperationLog;
import org.springframework.stereotype.Repository;


/**
 * 操作日志
 *
 * @author swb
 * @date 2021/08/10
 */
@Repository
public interface OperationLogDao extends BaseMapper<OperationLog> {
}
