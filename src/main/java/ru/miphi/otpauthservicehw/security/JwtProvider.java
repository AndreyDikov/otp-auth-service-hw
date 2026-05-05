package ru.miphi.otpauthservicehw.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nonnull;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.miphi.otpauthservicehw.enums.UserRole;
import ru.miphi.otpauthservicehw.properties.JwtProperties;

import javax.crypto.SecretKey;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;
import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class JwtProvider {

    SecretKey secretKey;

    Long expirationMs;

    public JwtProvider(JwtProperties jwtProperties) {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(UTF_8));
        this.expirationMs = jwtProperties.expirationMs();
    }

    public String generateToken(
            @Nonnull Long userId,
            String login,
            @Nonnull UserRole role
    ) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("login", login)
                .claim("role", role.name())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
