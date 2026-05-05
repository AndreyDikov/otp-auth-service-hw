package ru.miphi.otpauthservicehw.config;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.miphi.otpauthservicehw.controller.AdminController;
import ru.miphi.otpauthservicehw.controller.OtpController;
import ru.miphi.otpauthservicehw.security.JwtAuthenticationFilter;
import ru.miphi.otpauthservicehw.security.SecurityErrorResponseWriter;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static ru.miphi.otpauthservicehw.controller.AuthController.LOGIN;
import static ru.miphi.otpauthservicehw.controller.AuthController.REGISTER;
import static ru.miphi.otpauthservicehw.enums.UserRole.ADMIN;
import static ru.miphi.otpauthservicehw.enums.UserRole.USER;
import static ru.miphi.otpauthservicehw.exception.ErrorType.ACCESS_DENIED;
import static ru.miphi.otpauthservicehw.exception.ErrorType.AUTHORIZATION_HEADER_NOT_FOUND;
import static ru.miphi.otpauthservicehw.security.JwtAuthenticationFilter.*;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class SecurityConfig {

    private static final String ALL = "/**";
    public static final String ADMIN_ALL = AdminController.BASE + ALL;
    public static final String OTP_ALL = OtpController.BASE + ALL;
    public static final String V3_API_DOCS_ALL = V3_API_DOCS + ALL;
    public static final String SWAGGER_UI_ALL = SWAGGER_UI + ALL;

    JwtAuthenticationFilter jwtAuthenticationFilter;

    SecurityErrorResponseWriter securityErrorResponseWriter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(STATELESS)
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, exception) ->
                                securityErrorResponseWriter.write(response, AUTHORIZATION_HEADER_NOT_FOUND)
                        )
                        .accessDeniedHandler((request, response, exception) ->
                                securityErrorResponseWriter.write(response, ACCESS_DENIED)
                        )
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(POST, REGISTER).permitAll()
                        .requestMatchers(POST, LOGIN).permitAll()

                        .requestMatchers(V3_API_DOCS_ALL).permitAll()
                        .requestMatchers(SWAGGER_UI_ALL).permitAll()
                        .requestMatchers(SWAGGER_UI_HTML).permitAll()
                        .requestMatchers(V3_API_DOCS_YAML).permitAll()

                        .requestMatchers(ADMIN_ALL).hasRole(ADMIN.name())
                        .requestMatchers(OTP_ALL).hasRole(USER.name())

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
