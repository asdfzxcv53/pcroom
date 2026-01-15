package com.example.pcroom.infrastructure.security;

import com.example.pcroom.domain.User;
import com.example.pcroom.infrastructure.RemainTimeRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final RemainTimeRepository remainTimeRepository;

    public JwtAuthenticationFilter(final JwtUtil jwtUtil, final UserDetailsService userDetailsService, RemainTimeRepository remainTimeRepository) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.remainTimeRepository = remainTimeRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        } // access 토큰이 header 에 잘 들어왔는지 확인

        String token = header.substring(7);
        String username = jwtUtil.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // token 을 검증하고, username 이 있고, 인증객체가 securityContext 안에 없으면 실행된다.
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if(jwtUtil.validateToken(token, userDetails)) { // token 의 username 과 비교하고, expiration time 체크한다.
                User user = (User) userDetails;
                Long userId = user.getId();
                LocalDateTime endTime = remainTimeRepository.findEndTimeByUserId(userId)
                        .orElse(null);

                if(endTime == null && endTime.isBefore(LocalDateTime.now())) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                } // 이부분은 피시방로직에서 endtime 이 null 이거나 지금보다 전이면 로그아웃 상태라는 것으로 인지되어 바로 상태를 바꿔 return 해준다.

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
