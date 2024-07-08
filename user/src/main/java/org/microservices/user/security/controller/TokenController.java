package org.microservices.user.security.controller;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.microservices.user.model.User;
import org.microservices.user.security.exceptions.BadRequestException;
import org.microservices.user.security.util.TokenUtil;
import org.microservices.user.service.user.UserService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class TokenController {

    private final UserService userService;
    private final TokenUtil tokenUtil;

    public TokenController(UserService userService, TokenUtil tokenUtil) {
        this.userService = userService;
        this.tokenUtil = tokenUtil;
    }

    @CrossOrigin("*")
    @GetMapping(value = "/auth/refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String curRefreshToken = tokenUtil.getToken(request);
        if (curRefreshToken != null) {
            try {
                DecodedJWT decodedJWT = tokenUtil.tokenDecodeAndVerify(curRefreshToken);
                String username = decodedJWT.getSubject();
                User user = userService.findByUsername(username);
                String accessToken = tokenUtil.encodeAccessToken(user);
                String refreshToken = tokenUtil.encodeRefreshToken(user);
                tokenUtil.tokensToJSON(response, accessToken, refreshToken);
            } catch (IOException exception) {
                throw new BadRequestException("Problem during reading request payload: " + exception);
            }
            catch (TokenExpiredException expiredException) {
                throw new BadRequestException("Refresh denied, token had expired");
            }
        } else {
            throw new BadRequestException(
                    ("Invalid token format!")
            );
        }
    }
}
