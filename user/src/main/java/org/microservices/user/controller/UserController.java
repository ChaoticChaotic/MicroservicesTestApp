package org.microservices.user.controller;

import org.microservices.user.model.User;
import org.microservices.user.security.DTO.ChangePassRequest;
import org.microservices.user.security.DTO.RegistrationRequest;
import org.microservices.user.security.DTO.TokensDTO;
import org.microservices.user.security.DTO.mapper.UserMapper;
import org.microservices.user.security.util.TokenUtil;
import org.microservices.user.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping(value = "/api/users")
public class UserController {

    private final UserService userService;
    private final TokenUtil tokenUtil;
    private final UserMapper userMapper;

    public UserController(UserService userService,
                          TokenUtil tokenUtil,
                          UserMapper userMapper) {
        this.userService = userService;
        this.tokenUtil = tokenUtil;
        this.userMapper = userMapper;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll() { //Should return DTO instead of actual entity
        return ResponseEntity.ok().body(userService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) { //Should return DTO instead of actual entity
        return ResponseEntity.ok().body(userService.getById(id));
    }

    @PostMapping
    public ResponseEntity<User> save(@RequestBody RegistrationRequest registrationRequest) {
        return ResponseEntity.status(201).body(userService.save(userMapper.requestToUser(registrationRequest)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @Valid @RequestBody User user) {
        return ResponseEntity.ok().body(userService.update(id, user)); //Should get and return DTO instead of actual entity
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/password")
    public ResponseEntity<TokensDTO> changePassword(@RequestHeader(value = AUTHORIZATION) String accessToken,
                                                    @RequestBody @Valid ChangePassRequest request) {
        User user = userService.changePassword(tokenUtil.getUserIdFromToken(accessToken), request);
        return ResponseEntity.ok().body(TokensDTO.builder()
                .token(tokenUtil.encodeAccessToken(user))
                .refreshToken(tokenUtil.encodeRefreshToken(user))
                .build());
    }


}
