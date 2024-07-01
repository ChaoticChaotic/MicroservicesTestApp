package org.microservices.user.security.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.microservices.user.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Component
public class TokenUtil {

    @Value("${app.name}")
    private String APP_NAME;

    @Value("${jwt.secret}")
    private String SECRET;

    private final String HEADER_PREFIX = "Bearer ";

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(SECRET.getBytes());
    }

    public String encodeAccessToken(User user) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withAudience()
                .withExpiresAt( )
                .withIssuer(APP_NAME)
                .withClaim("id", user.getId())
                .withClaim("roles", user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .sign(getAlgorithm());
    }

    public String encodeRefreshToken(User appUser) {
        return JWT.create()
                .withSubject(appUser.getUsername())
                .withExpiresAt()
                .withIssuer(APP_NAME)
                .sign(getAlgorithm());
    }

    public DecodedJWT tokenDecodeAndVerify(String tokenToDecode) {
        Algorithm algorithm = getAlgorithm();
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(tokenToDecode);
    }

    public Map<String, String> tokensToJSON(HttpServletResponse response, String accessToken,
                                            String refreshToken) throws IOException {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("token", accessToken);
        tokens.put("refresh_token", refreshToken);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        return tokens;
    }

    public void tokensToResponse(HttpServletResponse response,
                                 String accessToken,
                                 String refreshToken) throws IOException {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("token", accessToken);
        tokens.put("refresh_token", refreshToken);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }

    public static String getAuthHeaderFromHeader(HttpServletRequest request ) {
        return request.getHeader(AUTHORIZATION);
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = getAuthHeaderFromHeader(request);
        if (authHeader != null && authHeader.startsWith(HEADER_PREFIX)) {
            return authHeader.substring(HEADER_PREFIX.length());
        }
        return null;
    }

    public UsernamePasswordAuthenticationToken getContextTokenFromDecodedJWT(DecodedJWT decodedJWT) {
        String username = decodedJWT.getSubject();
        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        stream(roles).forEach(
                role -> authorities.add(new SimpleGrantedAuthority(role))
        );
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    public Long getUserIdFromToken(String token) {
        String tokenToDecode = token.substring(HEADER_PREFIX.length());
        DecodedJWT curToken = JWT.decode(tokenToDecode);
        return Long.parseLong(curToken.getClaim("id").toString());
    }

    public List<String> getRoleFromToken(String token) {
        String tokenToDecode = token.substring(HEADER_PREFIX.length());
        DecodedJWT curToken = JWT.decode(tokenToDecode);
        return curToken.getClaim("roles").asList(String.class);
    }

}
