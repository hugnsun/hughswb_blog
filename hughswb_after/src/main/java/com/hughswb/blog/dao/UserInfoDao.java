package com.hughswb.blog.dao;

import com.hughswb.blog.entity.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;


/**
 * 用户信息
 *
 * @author swb
 * @date 2021/08/10
 */
@Repository
public interface UserInfoDao extends BaseMapper<UserInfo> {

}
