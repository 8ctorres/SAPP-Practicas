package com.tasks.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;
        
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Override
    public void configure(WebSecurity web) throws Exception {
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .addFilter(new JwtAuthorizationFilter(tokenProvider, authenticationManager()))
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,  "/swagger-ui.html").permitAll()
                .antMatchers(HttpMethod.GET,  "/swagger-resources/**").permitAll()
                .antMatchers(HttpMethod.GET,  "/v2/api-docs").permitAll()
                .antMatchers(HttpMethod.GET,  "/webjars/**").permitAll()
                .antMatchers(HttpMethod.GET,  "/javascript-libs/**").permitAll()
                .antMatchers(HttpMethod.GET,  "/css/**").permitAll()
                .antMatchers(HttpMethod.GET,  "/react-libs/**").permitAll()
                .antMatchers(HttpMethod.GET,  "/application/**").permitAll()
                .antMatchers(HttpMethod.POST,  "/api/login").permitAll()
                .antMatchers(HttpMethod.GET,  "/dashboard/").permitAll()
                .antMatchers(HttpMethod.GET,  "/dashboard/*").permitAll()
                .antMatchers(HttpMethod.GET,  "/api/users").permitAll()
                .antMatchers(HttpMethod.GET,  "/api/projects").permitAll()
                .antMatchers(HttpMethod.POST,  "/api/projects").permitAll()
                .antMatchers(HttpMethod.GET,  "/api/projects/*").permitAll()
                .antMatchers(HttpMethod.PUT,  "/api/projects/*").permitAll()
                .antMatchers(HttpMethod.DELETE,  "/api/projects/*").permitAll()
                .antMatchers(HttpMethod.GET,  "/api/projects/*/tasks").permitAll()
                .antMatchers(HttpMethod.GET,  "/api/comments").permitAll()
                .antMatchers(HttpMethod.GET,  "/api/comments/*").permitAll()
                .antMatchers(HttpMethod.GET,  "/api/tasks").permitAll()
                .antMatchers(HttpMethod.GET,  "/api/tasks/*").permitAll()
                .antMatchers(HttpMethod.PUT,  "/api/tasks/*").permitAll()
                .antMatchers(HttpMethod.DELETE,  "/api/tasks/*").permitAll()
                .antMatchers(HttpMethod.POST,  "/api/tasks/*/changeProgress").permitAll()
                .antMatchers(HttpMethod.POST,  "/api/tasks/*/changeResolution").permitAll()
                .antMatchers(HttpMethod.POST,  "/api/tasks/*/changeState").permitAll()
                .anyRequest().denyAll();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }
    
    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return authenticationManager();
    }

}
