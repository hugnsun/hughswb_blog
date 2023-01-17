package com.hughswb.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hughswb.blog.dao.*;
import com.hughswb.blog.dto.UserDetailDTO;
import com.hughswb.blog.entity.LoginUser;
import com.hughswb.blog.entity.UserAuth;
import com.hughswb.blog.entity.UserInfo;
import com.hughswb.blog.exception.BizException;
import com.hughswb.blog.service.RedisService;
import com.hughswb.blog.util.IpUtils;
import com.hughswb.blog.util.JwtUtil;
import com.hughswb.blog.util.RedisCache;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.hughswb.blog.constant.RedisPrefixConst.*;
import static com.hughswb.blog.enums.ZoneEnum.SHANGHAI;


/**
 * 用户详细信息服务
 *
 * @author swb
 * @date 2021/08/10
 */
@Service
@AllArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserAuthDao userAuthDao;

    private UserInfoDao userInfoDao;

    private RoleDao roleDao;

    private RedisService redisService;

    private HttpServletRequest request;

    private RedisCache redisCache;

    @Override
    public UserDetails loadUserByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            throw new BizException("用户名不能为空！");
        }
        // 查询账号是否存在
        UserAuth userAuth = userAuthDao.selectOne(new LambdaQueryWrapper<UserAuth>()
                .select(UserAuth::getId, UserAuth::getUserInfoId, UserAuth::getUsername, UserAuth::getPassword, UserAuth::getLoginType)
                .eq(UserAuth::getUsername, username));
        if (Objects.isNull(userAuth)) {
            throw new BizException("用户名不存在!");
        }

        // 获取用户所拥有的数据权限
        List<String> jurisdictionInfo = userAuthDao.getJurisdictionInfo(userAuth.getUserInfoId());
        // 封装登录信息
        return convertUserDetail(userAuth,request);
    }

    /**
     * 封装用户登录信息
     *
     * @param user    用户账号
     * @param request 请求
     * @return 用户登录信息
     */
    public UserDetailDTO convertUserDetail(UserAuth user, HttpServletRequest request) {
        // 查询账号信息
        UserInfo userInfo = userInfoDao.selectById(user.getUserInfoId());
        // 查询账号角色
        List<String> roleList = roleDao.listRolesByUserInfoId(userInfo.getId());
        // 查询账号点赞信息
        Set<Object> articleLikeSet = redisService.sMembers(ARTICLE_USER_LIKE + userInfo.getId());
        Set<Object> commentLikeSet = redisService.sMembers(COMMENT_USER_LIKE + userInfo.getId());
        Set<Object> talkLikeSet = redisService.sMembers(TALK_USER_LIKE + userInfo.getId());
        // 获取设备信息
        String ipAddress = IpUtils.getIpAddress(request);
        String ipSource = IpUtils.getIpSource(ipAddress);
        UserAgent userAgent = IpUtils.getUserAgent(request);
        String userId = String.valueOf(user.getUserInfoId());
        String jwt = JwtUtil.createJWT(userId);

        // 封装权限集合
        UserDetailDTO detailDTO = UserDetailDTO.builder()
                .id(user.getId())
                .loginType(user.getLoginType())
                .userInfoId(userInfo.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(userInfo.getEmail())
                .roleList(roleList)
                .nickname(userInfo.getNickname())
                .avatar(userInfo.getAvatar())
                .intro(userInfo.getIntro())
                .webSite(userInfo.getWebSite())
                .articleLikeSet(articleLikeSet)
                .commentLikeSet(commentLikeSet)
                .talkLikeSet(talkLikeSet)
                .ipAddress(ipAddress)
                .ipSource(ipSource)
                .token(jwt)
                .isDisable(userInfo.getIsDisable())
                .browser(userAgent.getBrowser().getName())
                .os(userAgent.getOperatingSystem().getName())
                .lastLoginTime(LocalDateTime.now(ZoneId.of(SHANGHAI.getZone())))
                .build();

        // 存放到Redis
        redisCache.setCacheObject("login"+userId,detailDTO);
        boolean expire = redisCache.expire("login" + userId, 1800);
        log.info("设置超时时间是否成功 key：{},  是否成功：{}","login"+userId,expire);
        return detailDTO;
    }

}
