package org.microservices.user.security.filters;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.microservices.user.security.exceptions.BadRequestException;
import org.microservices.user.security.util.TokenUtil;
import org.microservices.user.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.stream.Collectors;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final UserService appUserService;
    private final TokenUtil tokenUtil;

    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                UserService appUserService,
                                TokenUtil tokenUtil) {
        this.authenticationManager = authenticationManager;
        this.appUserService = appUserService;
        this.tokenUtil = tokenUtil;
    }

    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        System.out.println("we here");
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken("", "");
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication) throws IOException {

        User user = (User) authentication.getPrincipal();
        org.microservices.user.model.User curUser = appUserService.findByUsername(user.getUsername());
        String accessToken = tokenUtil.encodeAccessToken(curUser);
        String refreshToken = tokenUtil.encodeRefreshToken(curUser);
        tokenUtil.tokensToResponse(response, accessToken, refreshToken);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        response.sendError(401, "Unauthorized");
    }
}


