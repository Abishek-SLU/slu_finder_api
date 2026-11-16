
package com.app.security;

import static com.app.util.Constants.ACCESS_TOKEN_VALIDITY_SECONDS;
import static com.app.util.Constants.SIGNING_KEY;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.app.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(
            String token,
            Function<Claims, T> resolver) {

        Claims claims = getAllClaimsFromToken(token);
        return resolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token)
                .before(new Date());
    }

    public String generateToken(User user) {

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(user.getUserName())
                .setIssuer("SLU-Finder")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(
                    now + ACCESS_TOKEN_VALIDITY_SECONDS * 1000L))
                .signWith(
                    SignatureAlgorithm.HS256,
                    SIGNING_KEY
                )
                .compact();
    }

    public boolean validateToken(
            String token,
            UserDetails userDetails) {

        String username = getUsernameFromToken(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token)
                && userDetails.isEnabled();
    }
}
