package org.balaur.financemanagement.components;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.balaur.financemanagement.response.auth.AuthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class UserAuthenticationProvider {
    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    public String createToken(AuthResponse userDetailsResponse) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MONTH, 6); // for now allow token to be valid for 6 months

        Date validity = calendar.getTime();

        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return JWT.create()
                .withSubject(userDetailsResponse.getEmail())
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withClaim("username", userDetailsResponse.getUsername())
                .withClaim("roles", userDetailsResponse.getUserGroupCodes())
                .withClaim("currencyCode", userDetailsResponse.getCurrencyCode())
                .sign(algorithm);
    }

    public Authentication validateToken(String token) {
        return getAlgorithm(token);
    }

    public Authentication validateTokenStrongly(String token) {
        try {
            return getAlgorithm(token);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Token validation failed", ex);
        }
    }

    private Authentication getAlgorithm(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            AuthResponse userDetailsResponse = AuthResponse.builder()
                    .email(decodedJWT.getSubject())
                    .username(decodedJWT.getClaim("username").asString())
                    .build();

            return new UsernamePasswordAuthenticationToken(userDetailsResponse, null, Collections.emptyList());
        } catch (Exception ex) {
            System.err.println("Error in token verification: " + ex.getMessage());
            throw new RuntimeException("Error verifying token", ex);
        }
    }
}
