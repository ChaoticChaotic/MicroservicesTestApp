package org.microservices.user.security.filters;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.java.Log;
import org.microservices.user.security.util.TokenUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Log
public class AuthorizationFilter extends OncePerRequestFilter {

    private final TokenUtil tokenUtil;

    public AuthorizationFilter(TokenUtil tokenUtil) {
        this.tokenUtil = tokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (request.getServletPath().contains("/api/auth")) {
            filterChain.doFilter(request, response);
        } else {
            String accessToken = tokenUtil.getToken(request);
            if (accessToken != null) {
                try {
                    DecodedJWT decodedJWT = tokenUtil.tokenDecodeAndVerify(accessToken);
                    SecurityContextHolder.getContext()
                            .setAuthentication(tokenUtil.getContextTokenFromDecodedJWT(decodedJWT));
                    filterChain.doFilter(request, response);
                } catch (JWTVerificationException e) {
                    log.warning("JWTError: " + e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
