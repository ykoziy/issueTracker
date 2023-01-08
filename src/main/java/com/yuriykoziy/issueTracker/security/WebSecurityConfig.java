package com.yuriykoziy.issueTracker.security;

import com.yuriykoziy.issueTracker.services.UserProfileService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {
    private final UserProfileService userProfileService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .authenticationProvider(daoAuthenticationProvider())
                .authorizeRequests()
                .antMatchers("/api/v*/registration").permitAll()
                .antMatchers("/api/v*/basicauth").permitAll()
                .antMatchers("/api/v*/issue/**").hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/api/v*/comment/**").hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/api/v*/profile/**").hasAnyAuthority("USER", "ADMIN")
                .anyRequest().authenticated()
                .and()
                .httpBasic();
        return http.build();
    }

    // Use UserDetailsService and PasswordEncoder to authenticate a username and password
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(userProfileService);
        return provider;
    }
}
