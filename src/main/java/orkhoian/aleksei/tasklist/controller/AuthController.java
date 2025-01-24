package orkhoian.aleksei.tasklist.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import orkhoian.aleksei.tasklist.domain.user.User;
import orkhoian.aleksei.tasklist.service.AuthService;
import orkhoian.aleksei.tasklist.service.UserService;
import orkhoian.aleksei.tasklist.dto.auth.JwtRequest;
import orkhoian.aleksei.tasklist.dto.auth.JwtResponse;
import orkhoian.aleksei.tasklist.dto.user.UserDto;
import orkhoian.aleksei.tasklist.dto.validation.OnCreate;
import orkhoian.aleksei.tasklist.mapper.UserMapper;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
@Tag(name = "Auth controller", description = "Auth API")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public AuthController(AuthService authService, UserService userService, UserMapper userMapper) {
        this.authService = authService;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public JwtResponse login(@Validated @RequestBody JwtRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/register")
    public UserDto register(@Validated(OnCreate.class) @RequestBody UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        User createdUser = userService.create(user);
        return userMapper.toDto(createdUser);
    }

    @PostMapping("/refresh")
    public JwtResponse refresh(@RequestBody String refreshToken) {
        return authService.refresh(refreshToken);
    }
}
