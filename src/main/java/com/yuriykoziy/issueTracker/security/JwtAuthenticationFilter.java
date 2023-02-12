package com.yuriykoziy.issueTracker.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.yuriykoziy.issueTracker.models.UserProfile;
import com.yuriykoziy.issueTracker.security.jwt.JwtService;
import com.yuriykoziy.issueTracker.services.UserProfileService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
   private final JwtService jwtService;
   private final UserProfileService userProfileService;

   @Override
   protected void doFilterInternal(
         HttpServletRequest request,
         HttpServletResponse response,
         FilterChain filterChain) throws ServletException, IOException {
      final String authHeader = request.getHeader("Authorization");
      final String jwt;
      final String userName;
      if (authHeader == null || !authHeader.startsWith("Bearer")) {
         filterChain.doFilter(request, response);
         return;
      }
      jwt = authHeader.substring(7);
      userName = jwtService.extractUsername(jwt);
      if ((userName != null) && SecurityContextHolder.getContext().getAuthentication() == null) {
         UserProfile userProfile = userProfileService.loadUserByUsername(userName);
         if (jwtService.isTokenValid(jwt, userProfile) != null && jwtService.isTokenValid(jwt, userProfile)) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userName, null,
                  userProfile.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
         }
      }
      filterChain.doFilter(request, response);
   }
}