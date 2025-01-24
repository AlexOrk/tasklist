package orkhoian.aleksei.tasklist.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import orkhoian.aleksei.tasklist.domain.exception.ResourceNotFoundException;
import orkhoian.aleksei.tasklist.domain.user.Role;
import orkhoian.aleksei.tasklist.domain.user.User;
import orkhoian.aleksei.tasklist.service.UserService;
import orkhoian.aleksei.tasklist.dto.auth.JwtRequest;
import orkhoian.aleksei.tasklist.dto.auth.JwtResponse;
import orkhoian.aleksei.tasklist.security.JwtTokenProvider;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private final long id = 1L;
    private final String username = "username";
    private final String password = "password";
    private final User user = new User();
    private final JwtRequest request = new JwtRequest();

    @Test
    void login() {
        Set<Role> roles = Collections.emptySet();
        String accessToken = "access";
        String refreshToken = "refresh";
        request.setUsername(username);
        request.setPassword(password);
        user.setId(id);
        user.setUsername(username);
        user.setRoles(roles);

        when(userService.getByUsername(username)).thenReturn(user);
        when(tokenProvider.createAccessToken(id, username, roles)).thenReturn(accessToken);
        when(tokenProvider.createRefreshToken(id, username)).thenReturn(refreshToken);

        JwtResponse response = authService.login(request);

        assertEquals(username, response.getUsername());
        assertEquals(id, response.getId());
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
        verify(authenticationManager)
            .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    }

    @Test
    void loginInvalidUsername() {
        request.setUsername(username);
        request.setPassword(password);

        when(userService.getByUsername(username)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> authService.login(request));
        verifyNoInteractions(tokenProvider);
    }

    @Test
    void refresh() {
        String accessToken = "access";
        String refreshToken = "refresh";
        String anotherRefreshToken = "anotherRefresh";
        JwtResponse expected = new JwtResponse();
        expected.setAccessToken(accessToken);
        expected.setRefreshToken(anotherRefreshToken);

        when(tokenProvider.refreshUserTokens(refreshToken)).thenReturn(expected);

        JwtResponse actual = authService.refresh(refreshToken);

        assertEquals(expected, actual);
        verify(tokenProvider).refreshUserTokens(refreshToken);
    }
}
