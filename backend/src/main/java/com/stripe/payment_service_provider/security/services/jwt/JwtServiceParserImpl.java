package com.stripe.payment_service_provider.security.services.jwt;

import com.stripe.payment_service_provider.security.dto.TokenDTO;
import com.stripe.payment_service_provider.security.model.token.jwt.TokenScope;
import com.stripe.payment_service_provider.settings.exceptions.auth.InvalidJwtTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import java.security.PublicKey;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtServiceParserImpl {
    private final PublicKey accessTokenPublicKey;
    private final PublicKey refreshTokenPublicKey;


    public TokenDTO parseToken(String token) throws InvalidJwtTokenException {
        try {
            return TokenDTO.builder()
                    .token(token)
                    .userName(extractUsernameFromToken(token))
                    .roles(extractRolesFromToken(token))
                    .tokenScope(TokenScope.valueOf(extractScopeFromToken(token)))
                    .issuedAt(extractIssuedDateFromToken(token))
                    .expiresAt(extractExpirationFromToken(token))
                    .build();
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            throw new InvalidJwtTokenException("Invalid (JWT) token format", e);
        } catch (ExpiredJwtException e) {
            throw new InvalidJwtTokenException("Expired (JWT) token", e);
        } catch (InvalidClaimException e) {
            throw new InvalidJwtTokenException("Invalid value for claim \"" + e.getClaimName() + "\"", e);
        } catch (SignatureException e) {
            throw new InvalidJwtTokenException("Invalid token signature.", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidJwtTokenException("Invalid token.", e);
        }
    }

    public Claims extractClaim(String token, PublicKey publicKey){
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Claims extractAllClaims(String token) {
        try {
            return extractClaim(token, accessTokenPublicKey);
        } catch (SignatureException | InvalidKeyException e) {
            return extractClaim(token, refreshTokenPublicKey);
        }
    }

    private String extractTokenIdFromToken(@NotNull String token) {
        return extractAllClaims(token).getId();
    }

    private String extractUsernameFromToken(@NotNull String token) {
        return extractAllClaims(token).getSubject();
    }

    private Instant extractIssuedDateFromToken(@NotNull String token) {
        return extractAllClaims(token).getIssuedAt().toInstant();
    }

    private Instant extractExpirationFromToken(@NotNull String token) {
        return extractAllClaims(token).getExpiration().toInstant();
    }

    private Set<GrantedAuthority> extractRolesFromToken(@NotNull String token) {
        Claims claims = extractAllClaims(token);
        List<String> rolesAsString = claims.get("authorities", List.class);

        if (rolesAsString == null) {
            rolesAsString = new ArrayList<>();
        }
        return rolesAsString.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    private String extractScopeFromToken(@NotNull String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("scope", String.class);
    }
}
