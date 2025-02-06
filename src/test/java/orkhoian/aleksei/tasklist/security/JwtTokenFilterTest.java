package orkhoian.aleksei.tasklist.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.io.IOException;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtTokenFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private JwtTokenFilter jwtTokenFilter;

    @Test
    void doFilter() throws ServletException, IOException {
        String validToken = "valid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtTokenProvider.isValid(validToken)).thenReturn(true);
        when(jwtTokenProvider.getAuthentication(validToken)).thenReturn(authentication);
        doNothing().when(chain).doFilter(request, response);

        jwtTokenFilter.doFilter(request, response, chain);

        verify(jwtTokenProvider).isValid(validToken);
        verify(jwtTokenProvider).getAuthentication(validToken);
        verify(chain).doFilter(request, response);
    }
}
