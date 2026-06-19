package com.stripe.payment_service_provider.security.services.jwt;

import com.stripe.payment_service_provider.security.model.token.jwt.Token;
import com.stripe.payment_service_provider.user.model.Roles;
import com.stripe.payment_service_provider.user.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtServiceBuilder {
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.audience}")
    private String audience;
    public String buildToken(Token token, User userDetails, PrivateKey privateKey) {
        var authorities = userDetails.getRoles()
                .stream().
                 map(Roles::getName)
                .toList();
        return Jwts.builder()
                .id(token.getJti())
                .claim("authorities", authorities)
                .claim("scope", token.getTokenScope())
                .issuer(issuer)
                .audience().add(audience).and()
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(token.getIssuedAt()))
                .expiration(Date.from(token.getExpiresAt()))
                .signWith(privateKey)
                .compact();
    }
}
