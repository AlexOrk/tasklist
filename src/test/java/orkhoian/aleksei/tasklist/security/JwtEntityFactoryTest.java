package orkhoian.aleksei.tasklist.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import orkhoian.aleksei.tasklist.domain.user.Role;
import orkhoian.aleksei.tasklist.domain.user.User;
import orkhoian.aleksei.tasklist.utils.Helper;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JwtEntityFactoryTest {

    private final User user = User.builder()
        .id(1L)
        .username("username")
        .name("name")
        .password("password")
        .roles(Set.of(Role.ROLE_USER, Role.ROLE_ADMIN))
        .build();

    @Test
    void create() {
        JwtEntity expected = new JwtEntity(
            user.getId(),
            user.getUsername(),
            user.getName(),
            user.getPassword(),
            List.of(
                new SimpleGrantedAuthority(Role.ROLE_USER.name()),
                new SimpleGrantedAuthority(Role.ROLE_ADMIN.name())
            )
        );

        JwtEntity actual = JwtEntityFactory.create(user);

        List<GrantedAuthority> expectedAuthorities = Helper.getSortedAuthorities(expected.getAuthorities());
        List<GrantedAuthority> actualAuthorities = Helper.getSortedAuthorities(actual.getAuthorities());

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expectedAuthorities, actualAuthorities);
    }
}
