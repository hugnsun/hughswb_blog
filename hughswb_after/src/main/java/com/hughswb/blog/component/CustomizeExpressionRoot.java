package com.hughswb.blog.component;

import com.hughswb.blog.entity.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *  自定义获取权限信息
 */
@Component("hugh")
public class CustomizeExpressionRoot {
    /**
     * 是否有的权限
     *
     * @param authority 传过来的全校标识
     * @return 进行权限处理 如果当前登录用户存在权限就返回true
     */
    public boolean hasAuthority(String authority) {
        // 获取当前用户权限
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        List<String> permission = loginUser.getPermissions();
        // 判断用户权限集合中是否存在authority
        return permission.contains(authority);
    }
}
