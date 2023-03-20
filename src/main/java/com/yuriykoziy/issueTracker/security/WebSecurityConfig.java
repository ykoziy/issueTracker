package com.yuriykoziy.issueTracker.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.yuriykoziy.issueTracker.security.jwt.JwtAuthEntryPoint;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(new JwtAuthEntryPoint()).and()
                .authorizeRequests()
                .antMatchers("/api/v*/profile/unlock").hasAnyAuthority("ADMIN")
                .antMatchers("/api/v*/profile/ban").hasAnyAuthority("ADMIN")
                .antMatchers("/api/v*/auth/**").permitAll()
                .antMatchers("/api/v*/issue/**").hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/api/v*/comment/**").hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/api/v*/profile/**").hasAnyAuthority("USER", "ADMIN")
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
