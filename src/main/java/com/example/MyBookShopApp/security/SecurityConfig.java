package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.security.jwt.JwtRequestFilter;
import com.example.MyBookShopApp.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JwtRequestFilter filter;
    private final CustomOAuth2UserService oauthUserService;

    private final JwtService jwtService;

    @Autowired
    public SecurityConfig(BookstoreUserDetailsService bookstoreUserDetailsService, JwtRequestFilter filter, CustomOAuth2UserService oauthUserService, JwtService jwtService) {
        this.bookstoreUserDetailsService = bookstoreUserDetailsService;
        this.filter = filter;
        this.oauthUserService = oauthUserService;
        this.jwtService = jwtService;
    }

    @Bean
    PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(bookstoreUserDetailsService)
                .passwordEncoder(getPasswordEncoder());

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/books/my", "/books/page/my", "/my/archive", "/profile","/myslug","/books/looked","/books/page/looked").hasRole("USER")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/oauth/**", "/**").permitAll()
//                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/signin").failureUrl("/signin")
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/signin")
                .addLogoutHandler((httpServletRequest, httpServletResponse, authentication) -> {
                    Cookie[] cookies = httpServletRequest.getCookies();
                    if (cookies != null){
                        var cookieToken = Arrays.stream(cookies)
                                .filter(item -> item.getName().equals("token"))
                                .findFirst()
                                .orElse(null);
                        if (cookieToken != null) {
                            jwtService.addTokenToBlacklist(cookieToken.getValue());
                        }
                    }

                })
                .deleteCookies("token","postponedContents", "cartContents")
                .and().oauth2Client()
                .and().oauth2Login()
                .loginPage("/signin")
                .userInfoEndpoint()
                .userService(oauthUserService)
                .and().successHandler(new AuthenticationSuccessHandler() {

                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                        Authentication authentication) throws IOException {
                        logger.info("Google AuthenticationSuccessHandler invoked -  name: " + authentication.getName());

                        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();

                        oauthUserService.addOAuthUser(oauthUser);

                        response.sendRedirect("/books/my");
                    }
                })
                .failureHandler((httpServletRequest, httpServletResponse, e) -> logger.info("Google AuthenticationFailureHandler invoked Failure:"))
                .and()
                .exceptionHandling().accessDeniedPage("/403")
                .and()
                .logout().deleteCookies("token","postponedContents", "cartContents");

//        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }


}
