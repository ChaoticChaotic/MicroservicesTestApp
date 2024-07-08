package org.microservices.user.security.filters;

import com.fasterxml.jackson.databind.JsonNode;

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

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
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
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);
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
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}

