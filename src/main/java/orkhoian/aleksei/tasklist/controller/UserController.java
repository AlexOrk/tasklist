package orkhoian.aleksei.tasklist.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import orkhoian.aleksei.tasklist.domain.task.Task;
import orkhoian.aleksei.tasklist.domain.user.User;
import orkhoian.aleksei.tasklist.dto.nulab.TaskPublishParamsDto;
import orkhoian.aleksei.tasklist.service.NulabService;
import orkhoian.aleksei.tasklist.service.TaskService;
import orkhoian.aleksei.tasklist.service.UserService;
import orkhoian.aleksei.tasklist.dto.task.TaskDto;
import orkhoian.aleksei.tasklist.dto.user.UserDto;
import orkhoian.aleksei.tasklist.dto.validation.OnCreate;
import orkhoian.aleksei.tasklist.dto.validation.OnUpdate;
import orkhoian.aleksei.tasklist.mapper.TaskMapper;
import orkhoian.aleksei.tasklist.mapper.UserMapper;
import orkhoian.aleksei.tasklist.utils.Utils;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@Validated
@Tag(name = "User controller", description = "User API")
public class UserController {

    private final UserService userService;
    private final TaskService taskService;
    private final NulabService nulabService;
    private final UserMapper userMapper;
    private final TaskMapper taskMapper;

    @Autowired
    public UserController(
        UserService userService,
        TaskService taskService,
        NulabService nulabService,
        UserMapper userMapper,
        TaskMapper taskMapper
    ) {
        this.userService = userService;
        this.taskService = taskService;
        this.nulabService = nulabService;
        this.userMapper = userMapper;
        this.taskMapper = taskMapper;
    }

    @GetMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
    public UserDto getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return userMapper.toDto(user);
    }

    @GetMapping("/{id}/tasks")
    @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
    public List<TaskDto> getTasksByUserId(@PathVariable Long id) {
        List<Task> tasks = taskService.getAllByUserId(id);
        return taskMapper.toDto(tasks);
    }

    @PostMapping("/{id}/tasks")
    @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
    public TaskDto createTask(
        @PathVariable Long id,
        @Valid TaskPublishParamsDto params,
        @Validated(OnCreate.class) @RequestBody TaskDto dto
    ) {
        Task task = taskMapper.toEntity(dto);
        Task createdTask = taskService.create(task, id);
        if (params.getPublishInNulab() != null && params.getPublishInNulab()) {
            if (params.getProjectId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You must provide projectId if publish in Nulab");
            }
            nulabService.publishTaskInNulab(Utils.getCurrentUserApiKey(userService), params, dto);
        }
        return taskMapper.toDto(createdTask);
    }

    @PutMapping
    @PreAuthorize("@customSecurityExpression.canAccessUser(#dto.id)")
    public UserDto update(@Validated(OnUpdate.class) @RequestBody UserDto dto) {
        User user = userMapper.toEntity(dto);
        User updatedUser = userService.update(user);
        return userMapper.toDto(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.canAccessUser(#id)")
    public void deleteById(@PathVariable Long id) {
        userService.delete(id);
    }
}
