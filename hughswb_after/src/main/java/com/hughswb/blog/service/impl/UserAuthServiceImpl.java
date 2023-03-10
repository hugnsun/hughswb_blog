package com.hughswb.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hughswb.blog.dao.UserInfoDao;
import com.hughswb.blog.dao.UserRoleDao;
import com.hughswb.blog.dto.*;
import com.hughswb.blog.entity.LoginUser;
import com.hughswb.blog.entity.UserInfo;
import com.hughswb.blog.entity.UserAuth;
import com.hughswb.blog.dao.UserAuthDao;
import com.hughswb.blog.entity.UserRole;
import com.hughswb.blog.enums.LoginTypeEnum;
import com.hughswb.blog.enums.RoleEnum;
import com.hughswb.blog.exception.BizException;
import com.hughswb.blog.service.BlogInfoService;
import com.hughswb.blog.service.RedisService;
import com.hughswb.blog.service.UserAuthService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hughswb.blog.strategy.context.SocialLoginStrategyContext;
import com.hughswb.blog.util.*;
import com.hughswb.blog.vo.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.hughswb.blog.constant.CommonConst.*;
import static com.hughswb.blog.constant.MQPrefixConst.EMAIL_EXCHANGE;
import static com.hughswb.blog.constant.RedisPrefixConst.*;
import static com.hughswb.blog.enums.UserAreaTypeEnum.getUserAreaType;
import static com.hughswb.blog.util.CommonUtils.checkEmail;
import static com.hughswb.blog.util.CommonUtils.getRandomCode;


/**
 * ??????????????????
 *
 * @author swb
 * @date 2021/08/10
 */
@Service
@AllArgsConstructor
@Slf4j
public class UserAuthServiceImpl extends ServiceImpl<UserAuthDao, UserAuth> implements UserAuthService {

    private RedisService redisService;

    private AuthenticationManager authenticationManager;

    private RedisCache redisCache;

    private UserAuthDao userAuthDao;

    private UserRoleDao userRoleDao;

    private UserInfoDao userInfoDao;

    private BlogInfoService blogInfoService;

    private RabbitTemplate rabbitTemplate;

    private SocialLoginStrategyContext socialLoginStrategyContext;

    @Override
    public void sendCode(String email) {
        // ????????????????????????
        if (!checkEmail(email)) {
            throw new BizException("?????????????????????");
        }
        // ?????????????????????????????????
        String code = getRandomCode();
        // ???????????????
        EmailDTO emailDTO = EmailDTO.builder()
                .email(email)
                .subject("?????????")
                .content("??????????????????????????? ?????????????????? " + code + " ?????????15??????????????????????????????????????????????????? ??????????????????")
                .build();
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, "*", new Message(JSON.toJSONBytes(emailDTO), new MessageProperties()));
        // ??????????????????redis????????????????????????15??????
        redisService.set(USER_CODE_KEY + email, code, CODE_EXPIRE_TIME);
    }

    @Override
    public List listUserAreas(ConditionVO conditionVO) {
        List userAreaDTOList = new ArrayList<>();
        switch (Objects.requireNonNull(getUserAreaType(conditionVO.getType()))) {
            case USER:
                // ??????????????????????????????
                Object userArea = redisService.get(USER_AREA);
                if (Objects.nonNull(userArea)) {
                    userAreaDTOList = JSON.parseObject(userArea.toString(), List.class);
                }
                return userAreaDTOList;
            case VISITOR:
                // ????????????????????????
                Map<String, Object> visitorArea = redisService.hGetAll(VISITOR_AREA);
                if (Objects.nonNull(visitorArea)) {
                    userAreaDTOList = visitorArea.entrySet().stream()
                            .map(item -> UserAreaDTO.builder()
                                    .name(item.getKey())
                                    .value(Long.valueOf(item.getValue().toString()))
                                    .build())
                            .collect(Collectors.toList());
                }
                return userAreaDTOList;
            default:
                break;
        }
        return userAreaDTOList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(UserVO user) {
        // ????????????????????????
        if (checkUser(user)) {
            throw new BizException("????????????????????????");
        }
        // ??????????????????
        UserInfo userInfo = UserInfo.builder()
                .userName(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getUsername())
                .avatar(blogInfoService.getWebsiteConfig().getUserAvatar())
                .build();
        userInfoDao.insert(userInfo);
        // ??????????????????
        UserRole userRole = UserRole.builder()
                .userId(userInfo.getId())
                .roleId(RoleEnum.USER.getRoleId())
                .build();
        userRoleDao.insert(userRole);
        // ??????????????????
        UserAuth userAuth = UserAuth.builder()
                .userInfoId(userInfo.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()))
                .loginType(LoginTypeEnum.EMAIL.getType())
                .build();
        userAuthDao.insert(userAuth);
    }

    @Override
    public void updatePassword(UserVO user) {
        // ????????????????????????
        if (!checkUser(user)) {
            throw new BizException("????????????????????????");
        }
        // ???????????????????????????
        userAuthDao.update(new UserAuth(), new LambdaUpdateWrapper<UserAuth>()
                .set(UserAuth::getPassword, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()))
                .eq(UserAuth::getUsername, user.getUsername()));
    }

    @Override
    public void updateAdminPassword(PasswordVO passwordVO) {
        // ???????????????????????????
        UserAuth user = userAuthDao.selectOne(new LambdaQueryWrapper<UserAuth>()
                .eq(UserAuth::getId, UserUtils.getLoginUser().getId()));
        // ????????????????????????????????????????????????
        if (Objects.nonNull(user) && BCrypt.checkpw(passwordVO.getOldPassword(), user.getPassword())) {
            UserAuth userAuth = UserAuth.builder()
                    .id(UserUtils.getLoginUser().getId())
                    .password(BCrypt.hashpw(passwordVO.getNewPassword(), BCrypt.gensalt()))
                    .build();
            userAuthDao.updateById(userAuth);
        } else {
            throw new BizException("??????????????????");
        }
    }

    @Override
    public PageResult<UserBackDTO> listUserBackDTO(ConditionVO condition) {
        // ????????????????????????
        Integer count = userAuthDao.countUser(condition);
        if (count == 0) {
            return new PageResult<>();
        }
        // ????????????????????????
        List<UserBackDTO> userBackDTOList = userAuthDao.listUsers(PageUtils.getLimitCurrent(), PageUtils.getSize(), condition);
        return new PageResult<>(userBackDTOList, count);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserInfoDTO qqLogin(QQLoginVO qqLoginVO) {
        return socialLoginStrategyContext.executeLoginStrategy(JSON.toJSONString(qqLoginVO), LoginTypeEnum.QQ);
    }

    @Transactional(rollbackFor = BizException.class)
    @Override
    public UserInfoDTO weiboLogin(WeiboLoginVO weiboLoginVO) {
        return socialLoginStrategyContext.executeLoginStrategy(JSON.toJSONString(weiboLoginVO), LoginTypeEnum.WEIBO);
    }

    /**
     * ??????????????????????????????
     *
     * @param user ????????????
     * @return ??????
     */
    private Boolean checkUser(UserVO user) {
        if (!user.getCode().equals(redisService.get(USER_CODE_KEY + user.getEmail()))) {
            throw new BizException("??????????????????");
        }
        //???????????????????????????
        Integer count = userAuthDao.selectCount(new LambdaQueryWrapper<UserAuth>()
                .select(UserAuth::getUsername)
                .eq(UserAuth::getUsername, user.getUsername()));
        return count > 0;
    }

    /**
     * ??????????????????
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void statisticalUserArea() {
        // ????????????????????????
        Map<String, Long> userAreaMap = userAuthDao.selectList(new LambdaQueryWrapper<UserAuth>().select(UserAuth::getIpSource))
                .stream()
                .map(item -> {
                    if (StringUtils.isNotBlank(item.getIpSource())) {
                        return item.getIpSource().substring(0, 2)
                                .replaceAll(PROVINCE, "")
                                .replaceAll(CITY, "");
                    }
                    return UNKNOWN;
                })
                .collect(Collectors.groupingBy(item -> item, Collectors.counting()));
        // ????????????
        List<UserAreaDTO> userAreaList = userAreaMap.entrySet().stream()
                .map(item -> UserAreaDTO.builder()
                        .name(item.getKey())
                        .value(item.getValue())
                        .build())
                .collect(Collectors.toList());
        redisService.set(USER_AREA, JSON.toJSONString(userAreaList));
    }

}
