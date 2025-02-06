package orkhoian.aleksei.tasklist.security.expression;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import orkhoian.aleksei.tasklist.domain.exception.ResourceNotFoundException;
import orkhoian.aleksei.tasklist.domain.user.Role;
import orkhoian.aleksei.tasklist.service.TaskService;
import orkhoian.aleksei.tasklist.service.UserService;
import orkhoian.aleksei.tasklist.security.JwtEntity;

@Service("customSecurityExpression")
public class CustomSecurityExpression {

    private final UserService userService;
    private final TaskService taskService;

    @Autowired
    public CustomSecurityExpression(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    public boolean canAccessUser(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtEntity user = (JwtEntity) authentication.getPrincipal();
        Long userId = user.getId();

        return userId.equals(id) || hasAnyRole(authentication, Role.ROLE_ADMIN);
    }

    public boolean canAccessTask(Long taskId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtEntity user = (JwtEntity) authentication.getPrincipal();
        Long userId = user.getId();

        if (!taskService.isTaskExists(taskId)) {
            throw new ResourceNotFoundException("Task not found");
        }

        return userService.isTaskOwner(userId, taskId);
    }

    private boolean hasAnyRole(Authentication authentication, Role... roles) {
        for (var role : roles) {
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.name());
            if (authentication.getAuthorities().contains(authority)) {
                return true;
            }
        }
        return false;
    }
}
