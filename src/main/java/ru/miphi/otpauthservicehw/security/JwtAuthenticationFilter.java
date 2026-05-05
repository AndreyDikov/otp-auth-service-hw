package ru.miphi.otpauthservicehw.security;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.miphi.otpauthservicehw.enums.UserRole;

import java.io.IOException;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static ru.miphi.otpauthservicehw.controller.AuthController.BASE;
import static ru.miphi.otpauthservicehw.exception.ErrorType.AUTHORIZATION_HEADER_NOT_FOUND;
import static ru.miphi.otpauthservicehw.exception.ErrorType.INVALID_TOKEN;
import static ru.miphi.otpauthservicehw.security.SecurityAttribute.*;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ROLE_PREFIX = "ROLE_";

    public static final String V3_API_DOCS = "/v3/api-docs";
    public static final String V3_API_DOCS_YAML = V3_API_DOCS + ".yaml";
    public static final String SWAGGER_UI = "/swagger-ui";
    public static final String SWAGGER_UI_HTML = SWAGGER_UI + ".html";

    JwtProvider jwtProvider;
    SecurityErrorResponseWriter securityErrorResponseWriter;

    @Override
    protected boolean shouldNotFilter(@Nonnull HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith(BASE)
                || path.startsWith(V3_API_DOCS)
                || path.equals(V3_API_DOCS_YAML)
                || path.startsWith(SWAGGER_UI)
                || path.equals(SWAGGER_UI_HTML);
    }

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            securityErrorResponseWriter.write(response, AUTHORIZATION_HEADER_NOT_FOUND);
            return;
        }

        try {
            String token = authorizationHeader.substring(BEARER_PREFIX.length());
            Claims claims = jwtProvider.parseToken(token);

            Long userId = Long.valueOf(claims.getSubject());
            UserRole role = UserRole.valueOf(claims.get(ROLE, String.class));

            SecurityPrincipal principal = SecurityPrincipal.builder()
                    .userId(userId)
                    .login(claims.get(LOGIN, String.class))
                    .role(role)
                    .build();

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    List.of(new SimpleGrantedAuthority(ROLE_PREFIX + role.name()))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.setAttribute(USER_ID, userId);

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            SecurityContextHolder.clearContext();
            securityErrorResponseWriter.write(response, INVALID_TOKEN);
        }
    }

}
