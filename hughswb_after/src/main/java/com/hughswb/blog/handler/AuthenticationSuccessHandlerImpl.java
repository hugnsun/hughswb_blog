package com.hughswb.blog.handler;

import com.alibaba.fastjson.JSON;
import com.hughswb.blog.dao.UserAuthDao;
import com.hughswb.blog.dto.UserInfoDTO;
import com.hughswb.blog.entity.UserAuth;
import com.hughswb.blog.util.BeanCopyUtils;
import com.hughswb.blog.util.UserUtils;
import com.hughswb.blog.vo.Result;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static com.hughswb.blog.constant.CommonConst.APPLICATION_JSON;


/**
 * 登录成功处理
 *
 * @author swb
 * @date 2021/07/28
 */
@Component
@AllArgsConstructor
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    private UserAuthDao userAuthDao;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        // 返回登录信息
        UserInfoDTO userLoginDTO = BeanCopyUtils.copyObject(UserUtils.getLoginUser(), UserInfoDTO.class);
        httpServletResponse.setContentType(APPLICATION_JSON);
        httpServletResponse.getWriter().write(JSON.toJSONString(Result.ok(userLoginDTO)));
        // 更新用户ip，最近登录时间
        updateUserInfo();
    }

    /**
     * 更新用户信息
     */
    @Async
    public void updateUserInfo() {
        if (UserUtils.getLoginUser() != null) {
            UserAuth userAuth = UserAuth.builder()
                    .id(Objects.requireNonNull(UserUtils.getLoginUser()).getId())
                    .ipAddress(UserUtils.getLoginUser().getIpAddress())
                    .ipSource(UserUtils.getLoginUser().getIpSource())
                    .lastLoginTime(UserUtils.getLoginUser().getLastLoginTime())
                    .build();
            userAuthDao.updateById(userAuth);
        }
    }

}
