package orkhoian.aleksei.tasklist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import orkhoian.aleksei.tasklist.config.TestSecurityConfig;
import orkhoian.aleksei.tasklist.domain.exception.ExceptionBody;
import orkhoian.aleksei.tasklist.domain.user.User;
import orkhoian.aleksei.tasklist.dto.auth.JwtRefreshDto;
import orkhoian.aleksei.tasklist.dto.auth.JwtRequest;
import orkhoian.aleksei.tasklist.dto.auth.JwtResponse;
import orkhoian.aleksei.tasklist.dto.user.UserDto;
import orkhoian.aleksei.tasklist.mapper.UserMapper;
import orkhoian.aleksei.tasklist.service.AuthService;
import orkhoian.aleksei.tasklist.service.UserService;

import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
public class AuthControllerTest {

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Login successfully")
    void login() throws Exception {
        JwtRequest request = new JwtRequest("user@example.com", "password123");
        JwtResponse response = new JwtResponse(1L, "user@example.com", "mockAccessToken", "mockRefreshToken");

        when(authService.login(any(JwtRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Login failed, password must not be blank")
    void loginFail() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setUsername("user@example.com");
        ExceptionBody response = new ExceptionBody("Validation failed", Map.of("password", "must not be blank"));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Login failed, username must not be blank")
    void loginFail2() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setPassword("password123");
        ExceptionBody response = new ExceptionBody("Validation failed", Map.of("username", "must not be blank"));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Register successfully")
    void register() throws Exception {
        UserDto userDto = UserDto.builder()
            .name("John Doe")
            .username("user@example.com")
            .password("password123")
            .passwordConfirmation("password123")
            .build();

        User user = new User();

        String request = """
            {
                "name":"John Doe",
                "username":"user@example.com",
                "password":"password123",
                "passwordConfirmation":"password123"
            }
            """;

        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userService.create(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
    }

    @ParameterizedTest
    @MethodSource("provideDataForRegisterFail")
    @DisplayName("Register failed with validation exception")
    void registerFail(String request, String errorKey) throws Exception {
        ExceptionBody response = new ExceptionBody("Validation failed", Map.of(errorKey, "must not be blank"));

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Refresh token successfully")
    public void refresh() throws Exception {
        String refreshToken = "refreshToken";
        JwtRefreshDto refreshDto = new JwtRefreshDto(refreshToken);
        JwtResponse response = new JwtResponse();

        when(authService.refresh(refreshDto)).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshDto)))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Refresh token failed, refresh token must not be blank")
    public void refreshFail() throws Exception {
        String refreshToken = " ";
        JwtRefreshDto refreshDto = new JwtRefreshDto(refreshToken);
        ExceptionBody response = new ExceptionBody("Validation failed", Map.of("refreshToken", "must not be blank"));

        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshDto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    static Stream<Arguments> provideDataForRegisterFail() {
        return Stream.of(
            Arguments.of("""
                {
                    "username":"user@example.com",
                    "password":"password123",
                    "passwordConfirmation":"password123"
                }
                """,
                "name"
            ),
            Arguments.of("""
                {
                    "name":" ",
                    "username":"user@example.com",
                    "password":"password123",
                    "passwordConfirmation":"password123"
                }
                """,
                "name"
            ),
            Arguments.of("""
                {
                    "name":"John Doe",
                    "password":"password123",
                    "passwordConfirmation":"password123"
                }
                """,
                "username"
            ),
            Arguments.of("""
                {
                    "name":"John Doe",
                    "username":" ",
                    "password":"password123",
                    "passwordConfirmation":"password123"
                }
                """,
                "username"
            ),
            Arguments.of("""
                {
                    "name":"John Doe",
                    "username":"user@example.com",
                    "passwordConfirmation":"password123"
                }
                """,
                "password"
            ),
            Arguments.of("""
                {
                    "name":"John Doe",
                    "username":"user@example.com",
                    "password":" ",
                    "passwordConfirmation":"password123"
                }
                """,
                "password"
            ),
            Arguments.of("""
                {
                    "name":"John Doe",
                    "username":"user@example.com",
                    "password":"password123"
                }
                """,
                "passwordConfirmation"
            ),
            Arguments.of("""
                {
                    "name":"John Doe",
                    "username":"user@example.com",
                    "password":"password123",
                    "passwordConfirmation":" "
                }
                """,
                "passwordConfirmation"
            )
        );
    }
}
