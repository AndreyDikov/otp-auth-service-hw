package ru.miphi.otpauthservicehw.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.miphi.otpauthservicehw.service.JwtService;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtService jwtService
    ) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .anyRequest().permitAll()
        );

        http.addFilterBefore(
                new JwtRequestFilter(jwtService),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    static class JwtRequestFilter extends OncePerRequestFilter {

        private final JwtService jwtService;

        JwtRequestFilter(JwtService jwtService) {
            this.jwtService = jwtService;
        }

        @Override
        protected boolean shouldNotFilter(HttpServletRequest request) {
            String path = request.getRequestURI();
            return path.equals("/api/auth/register") || path.equals("/api/auth/login");
        }

        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain
        ) throws ServletException, IOException {
            String header = request.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            try {
                String token = header.substring("Bearer ".length());
                Claims claims = jwtService.parseToken(token);

                Long userId = Long.valueOf(claims.getSubject());
                String role = claims.get("role", String.class);

                if (request.getRequestURI().startsWith("/api/admin") && !"ADMIN".equals(role)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                request.setAttribute("userId", userId);
                request.setAttribute("role", role);

                filterChain.doFilter(request, response);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
    }
}
