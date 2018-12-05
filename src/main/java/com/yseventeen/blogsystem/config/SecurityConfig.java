package com.yseventeen.blogsystem.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 安全配置类
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  //启用方法安全设置
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Qualifier("userDetailsService")
    @Autowired
    private UserDetailsService userDetailsService;

    private static final String KEY = "yseventeen.com";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean  //标注在方法上(返回某个实例的方法)，等价于spring的xml配置文件中的<bean>，作用为：注册bean对象
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();  // 使用Bcry加密
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder); // 设置密码加密方式
        return authenticationProvider;
    }

    /**
     * 认证信息管理
     * <p>
     * inMemoryAuthentication采取内存认证的方式
     * jdbcAuthentication 基于数据表进行认证
     * 指定密码加密所使用的加密器为passwordEncoder()
     * 需要将密码加密后写入数据库
     *
     * @param auth
     * @throws Exception
     */

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // 认证信息存储在数据库中 通过userDatailsService从数据库中取出认证信息
        auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(authenticationProvider());

//        auth.inMemoryAuthentication()
//                .passwordEncoder(
//                        new BCryptPasswordEncoder())
//                .withUser("user")
//                .password(
//                        new BCryptPasswordEncoder().encode("123456")).roles("ADMIN");
    }

    /**
     * 重写config
     * 设置拦截策略
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                .antMatchers("/static/**/**", "/data/**", "/fonts/**", "/images/**", "/js/**", "/index")
                .permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/admins/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST,"/register").permitAll() // 需要相应的角色才能访问
                .and()
                .formLogin()   //基于 Form 表单登录验证
                .loginPage("/login").failureUrl("/login-error")
                .and().logout().logoutSuccessUrl("/index").permitAll()// 自定义登录界面
                .and().rememberMe().key(KEY)
                .and().exceptionHandling().accessDeniedPage("/403");  // 处理异常，拒绝访问就重定向到 403 页面


        http.csrf().ignoringAntMatchers("/h2-console/**"); // 禁用 H2 控制台的 CSRF 防护
        http.headers().frameOptions().sameOrigin(); // 允许来自同一来源的H2 控制台的请求

//        RequestMatcher requestMatcher = new CsrfSecurityRequestMatcher();
//        http.csrf().requireCsrfProtectionMatcher(requestMatcher);
    }

}
