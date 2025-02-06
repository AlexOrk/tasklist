package orkhoian.aleksei.tasklist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import orkhoian.aleksei.tasklist.config.WithMockJwtUser;
import orkhoian.aleksei.tasklist.domain.exception.ExceptionBody;
import orkhoian.aleksei.tasklist.domain.exception.ResourceNotFoundException;
import orkhoian.aleksei.tasklist.domain.task.Task;
import orkhoian.aleksei.tasklist.domain.user.User;
import orkhoian.aleksei.tasklist.dto.nulab.TaskPublishParamsDto;
import orkhoian.aleksei.tasklist.dto.task.TaskDto;
import orkhoian.aleksei.tasklist.dto.user.UserDto;
import orkhoian.aleksei.tasklist.mapper.TaskMapper;
import orkhoian.aleksei.tasklist.mapper.UserMapper;
import orkhoian.aleksei.tasklist.service.NulabService;
import orkhoian.aleksei.tasklist.service.TaskService;
import orkhoian.aleksei.tasklist.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private TaskService taskService;

    @MockBean
    private NulabService nulabService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private TaskMapper taskMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String userApiKey;

    @BeforeEach
    void setUp() {
        userApiKey = "test-api-key";

        User user = User.builder()
            .id(1L)
            .name("Test User")
            .username("test@example.com")
            .password("password123")
            .apiKey(userApiKey)
            .build();

        when(userService.getById(user.getId())).thenReturn(user);
    }

    @Test
    @DisplayName("Get user by id successfully")
    void getById() throws Exception {
        Long id = 1L;
        User user = User.builder().id(id).build();
        UserDto userDto = UserDto.builder().id(id).build();

        when(userService.getById(id)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        mockMvc.perform(get("/api/v1/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
    }

    @Test
    @DisplayName("Get user by id fail - user not found")
    void getByIdFail() throws Exception {
        Long id = 1L;
        String message = "User not found";

        when(userService.getById(id)).thenThrow(new ResourceNotFoundException(message));

        mockMvc.perform(get("/api/v1/users/{id}", id))
            .andExpect(status().isNotFound())
            .andExpect(content().json(objectMapper.writeValueAsString(new ExceptionBody(message))));
    }

    @Test
    @DisplayName("Get task by user id successfully")
    void getTasksByUserId() throws Exception {
        Long id = 1L;
        List<TaskDto> taskDtoList = List.of(new TaskDto());
        List<Task> tasks = List.of(new Task());

        when(taskService.getAllByUserId(id)).thenReturn(tasks);
        when(taskMapper.toDto(tasks)).thenReturn(taskDtoList);

        mockMvc.perform(get("/api/v1/users/{id}/tasks", id))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(taskDtoList)));
    }

    @Test
    @DisplayName("Create new task successfully")
    void createTask() throws Exception {
        Long id = 1L;
        TaskDto taskDto = TaskDto.builder().title("title").build();
        Task task = new Task();

        when(taskMapper.toEntity(taskDto)).thenReturn(task);
        when(taskService.create(task, id)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDto);

        mockMvc.perform(post("/api/v1/users/{id}/tasks", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDto)))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(taskDto)));
    }

    @Test
    @DisplayName("Create new task fail - validation failed")
    void createTaskFail() throws Exception {
        mockMvc.perform(post("/api/v1/users/{id}/tasks", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TaskDto())))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(objectMapper.writeValueAsString(
                new ExceptionBody("Validation failed", Map.of("title", "must not be blank")))));
    }

    @Test
    @DisplayName("Create new task and publish in Nulab successfully")
    @WithMockJwtUser
    void createTaskAndPublishInNulab() throws Exception {
        Long id = 1L;
        TaskDto taskDto = TaskDto.builder().title("title").build();
        Task task = new Task();
        TaskPublishParamsDto params = TaskPublishParamsDto.builder()
            .publishInNulab(true)
            .projectId(1L)
            .build();

        when(taskMapper.toEntity(taskDto)).thenReturn(task);
        when(taskService.create(task, id)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDto);
        doNothing().when(nulabService).publishTaskInNulab(userApiKey, params, taskDto);

        mockMvc.perform(post("/api/v1/users/{id}/tasks", id)
                .param("publishInNulab", "true")
                .param("projectId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDto)))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(taskDto)));
    }

    @Test
    @DisplayName("Create new task and publish in Nulab fail - validation failed")
    @WithMockJwtUser
    void createTaskAndPublishInNulabFail() throws Exception {
        Long id = 1L;
        TaskDto taskDto = TaskDto.builder().title("title").build();
        Task task = new Task();

        when(taskMapper.toEntity(taskDto)).thenReturn(task);
        when(taskService.create(task, id)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDto);

        mockMvc.perform(post("/api/v1/users/{id}/tasks", id)
                .param("publishInNulab", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(objectMapper.writeValueAsString(
                new ExceptionBody("400 BAD_REQUEST \"You must provide projectId if publish in Nulab\""))));
    }

    @Test
    @DisplayName("Update existing user successfully")
    void update() throws Exception {
        Long id = 1L;
        String name = "John Doe";
        String username = "user@example.com";
        String password = "password123";

        UserDto userDto = UserDto.builder()
            .id(id)
            .name(name)
            .username(username)
            .password(password)
            .build();

        UserDto returnUserDto = UserDto.builder()
            .id(id)
            .name(name)
            .username(username)
            .build();

        User user = new User();

        String request = """
            {
                "id": %d,
                "name": "%s",
                "username": "%s",
                "password": "%s"
            }
            """.formatted(id, name, username, password);

        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userService.update(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(returnUserDto);

        mockMvc.perform(put("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(returnUserDto)));
    }

    @ParameterizedTest
    @DisplayName("Update user validation failed")
    @MethodSource("provideDataForUpdateUserValidationFail")
    void updateFail(String request, Map<String, String> errors) throws Exception {

        mockMvc.perform(put("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(objectMapper.writeValueAsString(
                new ExceptionBody("Validation failed", errors))));
    }

    @Test
    @DisplayName("Delete user by id successfully")
    void deleteById() throws Exception {
        Long id = 1L;

        doNothing().when(userService).deleteById(id);

        mockMvc.perform(delete("/api/v1/users/{id}", id))
            .andExpect(status().isOk());

        verify(userService).deleteById(id);
    }

    static Stream<Arguments> provideDataForUpdateUserValidationFail() {
        Long id = 1L;
        String name = "John Doe";
        String username = "user@example.com";
        String password = "password123";

        return Stream.of(
            Arguments.of(
                """
                    {
                        "name": "%s",
                        "username": "%s",
                        "password": "%s"
                    }
                """.formatted(name, username, password),
                Map.of("id", "must not be null")
            ),
            Arguments.of(
                """
                    {
                        "id": %d,
                        "username": "%s",
                        "password": "%s"
                    }
                """.formatted(id, username, password),
                Map.of("name", "must not be blank")
            ),
            Arguments.of(
                """
                    {
                        "id": %d,
                        "name": "%s",
                        "password": "%s"
                    }
                """.formatted(id, username, password),
                Map.of("username", "must not be blank")
            ),
            Arguments.of(
                """
                    {
                        "id": %d,
                        "name": "%s",
                        "username": "%s"
                    }
                """.formatted(id, username, password),
                Map.of("password", "must not be blank")
            )
        );
    }
}
