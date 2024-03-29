package com.zxcv5595.reservation.security;

import com.zxcv5595.reservation.service.UserService;
import com.zxcv5595.reservation.type.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final String KEY_ROLES = "roles";
    private static final long EXPIRED_TIME = 1000 * 60 * 60; // 1 hour
    private final UserService userService;
    @Value("${spring.jwt.secret}")
    private String secretKey;

    public String generateToken(String username, Set<Role> userType) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES, userType);

        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + EXPIRED_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    @Transactional
    public Authentication getAuthentication(String jwt) {

        UserDetails userDetails = userService.loadUserByUsername( getUsername(jwt));

        return new UsernamePasswordAuthenticationToken(userDetails, "",
                userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return paresClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        Claims claims = paresClaims(token);
        return !claims.getExpiration().before(new Date());
    }

    private Claims paresClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }

    }
}