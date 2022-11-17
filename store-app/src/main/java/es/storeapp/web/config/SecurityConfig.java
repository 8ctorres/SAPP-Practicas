package es.storeapp.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and().csrf().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/").permitAll()
                .antMatchers(HttpMethod.GET, "/login").permitAll()
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .antMatchers(HttpMethod.GET, "/logout").permitAll()
                .antMatchers(HttpMethod.GET, "/profile").permitAll()
                .antMatchers(HttpMethod.POST, "/profile").permitAll()
                .antMatchers(HttpMethod.POST, "/profile/*").permitAll()
                .antMatchers(HttpMethod.GET, "/profile/image").permitAll()
                .antMatchers(HttpMethod.POST, "/profile/image/remove").permitAll()
                .antMatchers(HttpMethod.GET, "/registration").permitAll()
                .antMatchers(HttpMethod.POST, "/registration").permitAll()
                .antMatchers(HttpMethod.GET, "/orders").permitAll()
                .antMatchers(HttpMethod.POST, "/orders").permitAll()
                .antMatchers(HttpMethod.GET, "/orders/*").permitAll()
                .antMatchers(HttpMethod.POST, "/orders/*").permitAll()
                .antMatchers(HttpMethod.GET, "/orders/complete").permitAll()
                .antMatchers(HttpMethod.POST, "/orders/*/cancel").permitAll()
                .antMatchers(HttpMethod.GET, "/orders/*/pay").permitAll()
                .antMatchers(HttpMethod.POST, "/orders/*/pay").permitAll()
                .antMatchers(HttpMethod.GET, "/products").permitAll()
                .antMatchers(HttpMethod.GET, "/products/*").permitAll()
                .antMatchers(HttpMethod.POST, "/products/*/addToCart").permitAll()
                .antMatchers(HttpMethod.POST, "/products/*/removeFromCart").permitAll()
                .antMatchers(HttpMethod.GET, "/products/*/rate").permitAll()
                .antMatchers(HttpMethod.POST, "/products/*/rate").permitAll()
                .antMatchers(HttpMethod.GET, "/changePassword").permitAll()
                .antMatchers(HttpMethod.POST, "/changePassword").permitAll()
                .antMatchers(HttpMethod.GET, "/resetPassword").permitAll()
                .antMatchers(HttpMethod.POST, "/resetPassword").permitAll()
                .antMatchers(HttpMethod.GET, "/sendEmail").permitAll()
                .antMatchers(HttpMethod.POST, "/sendEmail").permitAll()
                .antMatchers(HttpMethod.GET, "/cart").permitAll()
                .antMatchers(HttpMethod.GET, "/resources/database/*").denyAll()
                .antMatchers(HttpMethod.GET, "/resources/{\\d+}/*").permitAll()
                .antMatchers(HttpMethod.GET, "/fonts/**").permitAll()
                .antMatchers(HttpMethod.GET, "/webjars/**").permitAll()
                .antMatchers(HttpMethod.GET, "/Scripts.js").permitAll()
                .antMatchers(HttpMethod.GET, "/Styles.css").permitAll()
                .anyRequest().denyAll()
                .and()
                .sessionManagement().sessionFixation().migrateSession()
                .and().headers().xssProtection()
                .and().contentSecurityPolicy("default-src 'self'; " +
                        "script-src 'self' 'unsafe-inline'; " +
                        "style-src 'self' 'unsafe-inline'; " +
                        "img-src 'self' data:; " +
                        "object-src 'none'; " +
                        "base-uri 'none'; ");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        source.registerCorsConfiguration("/**", config);

        return source;

    }

}

