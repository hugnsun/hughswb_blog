package com.hughswb.blog.config;


import com.hughswb.blog.filter.JwtAuthenticationTokenFilter;
import com.hughswb.blog.handler.AuthenticationFailHandlerImpl;
import com.hughswb.blog.handler.AuthenticationSuccessHandlerImpl;
import com.hughswb.blog.handler.LogoutSuccessHandlerImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *SpringSecurity的配置类
 * @author hughs
 */

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    private AuthenticationEntryPoint authenticationEntryPoint;

    private AccessDeniedHandler accessDeniedHandler;

    private AuthenticationSuccessHandlerImpl authenticationSuccessHandler;

    private AuthenticationFailHandlerImpl authenticationFailHandler;

    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    // 创建BCryptPasswordEncoder注入容器
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

//    @Bean
//    protected HttpSecurity configure(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity
//                // CSRF禁用，因为不使用session
//                .csrf().disable()
//                // 不通过Session获取SecurityContext
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                // 过滤请求
//                .authorizeRequests()
//                // 对于登录接口 允许匿名访问  登录之后就不能访问这个接口
//                .antMatchers("/login").anonymous()
//                // 除上面外的所有请求全部需要鉴权认证
//                .anyRequest().authenticated();
//        // 把token校验过滤器添加到过滤器链中
//        httpSecurity.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
//
//        // 配置异常处理器
//        httpSecurity.exceptionHandling()
//                // 配置认证失败处理器
//                .authenticationEntryPoint(authenticationEntryPoint)
//                // 配置授权失败处理器
//                .accessDeniedHandler(accessDeniedHandler);
//        // 允许跨域
//        httpSecurity.cors();
//        // 配置认证成功处理器
//        httpSecurity.formLogin()
//                .loginProcessingUrl("/login")
//                .successHandler(authenticationSuccessHandler)
//                // 配置认证失败处理器
//                .failureHandler(authenticationFailHandler);
//
//        httpSecurity.logout()
//                .logoutUrl("/loginOut")
//                // 配置注销成功处理器
//                .logoutSuccessHandler(logoutSuccessHandler);
//        return httpSecurity;
//    }
@Override
protected void configure(HttpSecurity http) throws Exception {
    http
            //关闭csrf
            .csrf().disable()
            //不通过Session获取SecurityContext
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            // 对于登录接口 允许匿名访问
            .antMatchers("/login").anonymous()
            // 除上面外的所有请求全部需要鉴权认证
            .anyRequest().authenticated()
            .and()
            //将jwt过滤器添加到UsernamePasswordAuthenticationFilter之前
            .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
            //配置自定义异常处理器
            .exceptionHandling()
            //认证失败处理器
            .authenticationEntryPoint(authenticationEntryPoint)
            //授权失败处理器
            .accessDeniedHandler(accessDeniedHandler);

    http.formLogin()
            .loginProcessingUrl("/api/login")
            .successHandler(authenticationSuccessHandler)
            // 配置认证失败处理器
            .failureHandler(authenticationFailHandler);
    http.logout()
                .logoutUrl("/api/loginOut")
                // 配置注销成功处理器
                .logoutSuccessHandler(logoutSuccessHandler);
}

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

}

