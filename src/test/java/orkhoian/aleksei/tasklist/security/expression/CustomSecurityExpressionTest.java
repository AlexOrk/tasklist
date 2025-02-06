package orkhoian.aleksei.tasklist.security.expression;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import orkhoian.aleksei.tasklist.domain.exception.ResourceNotFoundException;
import orkhoian.aleksei.tasklist.domain.user.Role;
import orkhoian.aleksei.tasklist.security.JwtEntity;
import orkhoian.aleksei.tasklist.service.TaskService;
import orkhoian.aleksei.tasklist.service.UserService;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomSecurityExpressionTest {

    @Mock
    private UserService userService;

    @Mock
    private TaskService taskService;

    @Mock
    private Authentication authentication;

    @Mock
    private JwtEntity jwtEntity;

    @InjectMocks
    private CustomSecurityExpression customSecurityExpression;

    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        customSecurityExpression = new CustomSecurityExpression(userService, taskService);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Can access user successfully when user owns the account")
    void canAccessUser() {
        when(authentication.getPrincipal()).thenReturn(jwtEntity);
        when(jwtEntity.getId()).thenReturn(userId);

        assertTrue(customSecurityExpression.canAccessUser(userId));
    }

    @Test
    @DisplayName("Can access user successfully when user is admin")
    void canAccessUserAdmin() {
        Long anotherUserId = 2L;

        when(authentication.getPrincipal()).thenReturn(jwtEntity);
        when(jwtEntity.getId()).thenReturn(userId);
        when(authentication.getAuthorities())
            .thenReturn((Collection) List.of(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name())));

        assertTrue(customSecurityExpression.canAccessUser(anotherUserId));
    }

    @Test
    @DisplayName("Can not access user when user is not the owner of the account and not an admin")
    void canAccessUserFalse() {
        Long anotherUserId = 2L;

        when(authentication.getPrincipal()).thenReturn(jwtEntity);
        when(jwtEntity.getId()).thenReturn(userId);
        when(authentication.getAuthorities()).thenReturn(List.of());

        assertFalse(customSecurityExpression.canAccessUser(anotherUserId));
    }

    @Test
    @DisplayName("Can access task successfully when user owns the task")
    void canAccessTask() {
        Long taskId = 100L;

        when(authentication.getPrincipal()).thenReturn(jwtEntity);
        when(jwtEntity.getId()).thenReturn(userId);
        when(taskService.isTaskExists(taskId)).thenReturn(true);
        when(userService.isTaskOwner(userId, taskId)).thenReturn(true);

        assertTrue(customSecurityExpression.canAccessTask(taskId));
    }

    @Test
    @DisplayName("Can not access task when user is not the owner of the task")
    void canAccessTaskFalse() {
        Long taskId = 100L;

        when(authentication.getPrincipal()).thenReturn(jwtEntity);
        when(jwtEntity.getId()).thenReturn(userId);
        when(taskService.isTaskExists(taskId)).thenReturn(true);
        when(userService.isTaskOwner(userId, taskId)).thenReturn(false);

        assertFalse(customSecurityExpression.canAccessTask(taskId));
    }

    @Test
    @DisplayName("Can not access task because task does not exist")
    void canAccessTaskFail() {
        Long taskId = 100L;

        when(authentication.getPrincipal()).thenReturn(jwtEntity);
        when(taskService.isTaskExists(taskId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> customSecurityExpression.canAccessTask(taskId), "Task not found");
    }
}
