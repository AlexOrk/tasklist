package orkhoian.aleksei.tasklist.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import orkhoian.aleksei.tasklist.domain.user.Role;
import orkhoian.aleksei.tasklist.domain.user.User;
import orkhoian.aleksei.tasklist.service.UserService;
import orkhoian.aleksei.tasklist.utils.Helper;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private JwtUserDetailsService jwtUserDetailsService;

    @Test
    @DisplayName("Load user by username")
    void loadUserByUsername() {
        String username = "username";
        User user = User.builder()
            .id(1L)
            .username(username)
            .name("name")
            .password("password")
            .roles(Set.of(Role.ROLE_USER, Role.ROLE_ADMIN))
            .build();

        JwtEntity jwtEntity = new JwtEntity(
            user.getId(),
            user.getUsername(),
            user.getName(),
            user.getPassword(),
            List.of(
                new SimpleGrantedAuthority(Role.ROLE_USER.name()),
                new SimpleGrantedAuthority(Role.ROLE_ADMIN.name())
            )
        );

        when(userService.getByUsername(username)).thenReturn(user);

        UserDetails result = jwtUserDetailsService.loadUserByUsername(username);
        List<GrantedAuthority> expectedAuthorities = Helper.getSortedAuthorities(jwtEntity.getAuthorities());
        List<GrantedAuthority> actualAuthorities = Helper.getSortedAuthorities(result.getAuthorities());

        assertEquals(jwtEntity.getUsername(), result.getUsername());
        assertEquals(jwtEntity.getPassword(), result.getPassword());
        assertEquals(expectedAuthorities, actualAuthorities);
    }
}
