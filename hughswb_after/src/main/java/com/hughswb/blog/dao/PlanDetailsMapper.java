package com.hughswb.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.hughswb.blog.entity.PlanDetails;
import org.apache.ibatis.annotations.Mapper;

/**
 * 计划明细表
 *
 * @author 变成派大星
 * @date 2023-02-01 21:13:25
 */
@Mapper
public interface PlanDetailsMapper extends BaseMapper<PlanDetails> {

}
