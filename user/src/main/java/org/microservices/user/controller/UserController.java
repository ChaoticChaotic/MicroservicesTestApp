package org.microservices.user.controller;

import org.microservices.user.model.User;
import org.microservices.user.security.DTO.ChangePassRequest;
import org.microservices.user.security.DTO.TokensDTO;
import org.microservices.user.security.util.TokenUtil;
import org.microservices.user.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping(value = "/api")
public class UserController {

    private final UserService appUserService;
    private final TokenUtil tokenUtil;

    @Autowired
    public UserController(UserService appUserService,
                          TokenUtil tokenUtil) {
        this.appUserService = appUserService;
        this.tokenUtil = tokenUtil;
    }

    @GetMapping("/auth/user")
    public ResponseEntity<User> user(Principal user) {
        return ResponseEntity.ok().body(appUserService.findByUsername(user.getName()));
    }

    @PutMapping("/user/password")
    public ResponseEntity<TokensDTO> changePassword(@RequestHeader(value = AUTHORIZATION) String accessToken,
                                                    @RequestBody @Valid ChangePassRequest request) {
        User user = appUserService.changePassword(tokenUtil.getUserIdFromToken(accessToken), request);
        return ResponseEntity.ok().body(TokensDTO.builder()
                .token(tokenUtil.encodeAccessToken(user))
                .refreshToken(tokenUtil.encodeRefreshToken(user))
                .build());
    }
}
