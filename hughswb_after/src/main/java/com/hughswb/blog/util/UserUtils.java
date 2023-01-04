package com.hughswb.blog.util;

import com.hughswb.blog.dto.UserDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


/**
 * 用户工具类
 *
 * @author swb
 * @date 2021/08/10
 */
@Component
@Slf4j
public class UserUtils {

    /**
     * 获取当前登录用户
     *
     * @return 用户登录信息
     */
    public static UserDetailDTO getLoginUser() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if ("anonymousUser".equals(String.valueOf(principal))) {
                return  null;
            }
            return  (UserDetailDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            log.error("出现异常 异常信息 {}",e.getMessage());
            return null;
        }
    }

}
