package com.hughswb.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hughswb.blog.constant.CommonConst;
import com.hughswb.blog.dao.UserInfoDao;
import com.hughswb.blog.dao.UserRoleDao;
import com.hughswb.blog.dto.*;
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
import com.hughswb.blog.util.PageUtils;
import com.hughswb.blog.util.UserUtils;
import com.hughswb.blog.vo.*;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
 * 用户账号服务
 *
 * @author swb
 * @date 2021/08/10
 */
@Service
@AllArgsConstructor
public class UserAuthServiceImpl extends ServiceImpl<UserAuthDao, UserAuth> implements UserAuthService {

    private RedisService redisService;

    private UserAuthDao userAuthDao;

    private UserRoleDao userRoleDao;

    private UserInfoDao userInfoDao;

    private BlogInfoService blogInfoService;

    private RabbitTemplate rabbitTemplate;

    private SocialLoginStrategyContext socialLoginStrategyContext;

    @Override
    public void sendCode(String email) {
        // 校验账号是否合法
        if (!checkEmail(email)) {
            throw new BizException("请输入正确邮箱");
        }
        // 生成六位随机验证码发送
        String code = getRandomCode();
        // 发送验证码
        EmailDTO emailDTO = EmailDTO.builder()
                .email(email)
                .subject("验证码")
                .content("变成派大星网站注册 您的验证码为 " + code + " 有效期15分钟，请不要告诉他人哦！变成派大星 祝您生活愉快")
                .build();
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, "*", new Message(JSON.toJSONBytes(emailDTO), new MessageProperties()));
        // 将验证码存入redis，设置过期时间为15分钟
        redisService.set(USER_CODE_KEY + email, code, CODE_EXPIRE_TIME);
    }

    @Override
    public List<UserAreaDTO> listUserAreas(ConditionVO conditionVO) {
        List<UserAreaDTO> userAreaDTOList = new ArrayList<>();
        switch (Objects.requireNonNull(getUserAreaType(conditionVO.getType()))) {
            case USER:
                // 查询注册用户区域分布
                Object userArea = redisService.get(USER_AREA);
                if (Objects.nonNull(userArea)) {
                    userAreaDTOList = JSON.parseObject(userArea.toString(), List.class);
                }
                return userAreaDTOList;
            case VISITOR:
                // 查询游客区域分布
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
        // 校验账号是否合法
        if (checkUser(user)) {
            throw new BizException("用户名已经存在！");
        }
        // 新增用户信息
        UserInfo userInfo = UserInfo.builder()
                .userName(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getUsername())
                .avatar(blogInfoService.getWebsiteConfig().getUserAvatar())
                .build();
        userInfoDao.insert(userInfo);
        // 绑定用户角色
        UserRole userRole = UserRole.builder()
                .userId(userInfo.getId())
                .roleId(RoleEnum.USER.getRoleId())
                .build();
        userRoleDao.insert(userRole);
        // 新增用户账号
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
        // 校验账号是否合法
        if (!checkUser(user)) {
            throw new BizException("用户名尚未注册！");
        }
        // 根据用户名修改密码
        userAuthDao.update(new UserAuth(), new LambdaUpdateWrapper<UserAuth>()
                .set(UserAuth::getPassword, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()))
                .eq(UserAuth::getUsername, user.getUsername()));
    }

    @Override
    public void updateAdminPassword(PasswordVO passwordVO) {
        // 查询旧密码是否正确
        UserAuth user = userAuthDao.selectOne(new LambdaQueryWrapper<UserAuth>()
                .eq(UserAuth::getId, UserUtils.getLoginUser().getId()));
        // 正确则修改密码，错误则提示不正确
        if (Objects.nonNull(user) && BCrypt.checkpw(passwordVO.getOldPassword(), user.getPassword())) {
            UserAuth userAuth = UserAuth.builder()
                    .id(UserUtils.getLoginUser().getId())
                    .password(BCrypt.hashpw(passwordVO.getNewPassword(), BCrypt.gensalt()))
                    .build();
            userAuthDao.updateById(userAuth);
        } else {
            throw new BizException("旧密码不正确");
        }
    }

    @Override
    public PageResult<UserBackDTO> listUserBackDTO(ConditionVO condition) {
        // 获取后台用户数量
        Integer count = userAuthDao.countUser(condition);
        if (count == 0) {
            return new PageResult<>();
        }
        // 获取后台用户列表
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
     * 校验用户数据是否合法
     *
     * @param user 用户数据
     * @return 结果
     */
    private Boolean checkUser(UserVO user) {
        if (!user.getCode().equals(redisService.get(USER_CODE_KEY + user.getEmail()))) {
            throw new BizException("验证码错误！");
        }
        //查询用户名是否存在
        Integer count = userAuthDao.selectCount(new LambdaQueryWrapper<UserAuth>()
                .select(UserAuth::getUsername)
                .eq(UserAuth::getUsername, user.getUsername()));
        return count > 0;
    }

    /**
     * 统计用户地区
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void statisticalUserArea() {
        // 统计用户地域分布
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
        // 转换格式
        List<UserAreaDTO> userAreaList = userAreaMap.entrySet().stream()
                .map(item -> UserAreaDTO.builder()
                        .name(item.getKey())
                        .value(item.getValue())
                        .build())
                .collect(Collectors.toList());
        redisService.set(USER_AREA, JSON.toJSONString(userAreaList));
    }

}
