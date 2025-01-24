package orkhoian.aleksei.tasklist.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import orkhoian.aleksei.tasklist.domain.exception.ResourceNotFoundException;
import orkhoian.aleksei.tasklist.domain.user.Role;
import orkhoian.aleksei.tasklist.domain.user.User;
import orkhoian.aleksei.tasklist.repository.UserRepository;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private final long id = 1L;
    private final String username = "username";
    private final String password = "password";
    private final User expectedUser = new User();

    @Test
    void getById() {
        expectedUser.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(expectedUser));

        User actual = userService.getById(id);

        assertEquals(expectedUser, actual);
        verify(userRepository).findById(id);
    }

    @Test
    void getByIdNotFound() {
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getById(id));
        verify(userRepository).findById(id);
    }

    @Test
    void getByUsername() {
        expectedUser.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(expectedUser));

        User actual = userService.getByUsername(username);

        assertEquals(expectedUser, actual);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void getByUsernameNotFound() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getByUsername(username));
        verify(userRepository).findByUsername(username);
    }

    @Test
    void update() {
        expectedUser.setPassword(password);

        userService.update(expectedUser);

        verify(passwordEncoder).encode(password);
        verify(userRepository).save(expectedUser);
    }

    @Test
    void isTaskOwner() {
        long taskId = 1L;

        when(userRepository.isTaskOwner(id, taskId)).thenReturn(true);

        boolean isOwner = userService.isTaskOwner(id, taskId);

        assertTrue(isOwner);
        verify(userRepository).isTaskOwner(id, taskId);
    }

    @Test
    void create() {
        Set<Role> expectedRoles = Set.of(Role.ROLE_USER);
        expectedUser.setUsername(username);
        expectedUser.setPassword(password);
        expectedUser.setPasswordConfirmation(password);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        User actual = userService.create(expectedUser);

        assertEquals(expectedRoles, actual.getRoles());
        verify(userRepository).findByUsername(username);
        verify(userRepository).save(expectedUser);
    }

    @Test
    void createExistingUser() {
        expectedUser.setUsername(username);
        expectedUser.setPassword(password);
        expectedUser.setPasswordConfirmation(password);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        assertThrows(IllegalStateException.class, () -> userService.create(expectedUser));
        verify(userRepository).findByUsername(username);
        verify(userRepository, never()).save(expectedUser);
    }

    @Test
    void createNotEqualsPasswords() {
        expectedUser.setUsername(username);
        expectedUser.setPassword(password);
        expectedUser.setPasswordConfirmation("another");

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> userService.create(expectedUser));
        verify(userRepository).findByUsername(username);
        verify(userRepository, never()).save(expectedUser);
    }

    @Test
    void delete() {
        userService.delete(id);
        verify(userRepository).deleteById(id);
    }
}
