package orkhoian.aleksei.tasklist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import orkhoian.aleksei.tasklist.config.TestSecurityConfig;
import orkhoian.aleksei.tasklist.domain.exception.ExceptionBody;
import orkhoian.aleksei.tasklist.domain.exception.ResourceNotFoundException;
import orkhoian.aleksei.tasklist.domain.task.Task;
import orkhoian.aleksei.tasklist.dto.task.TaskDto;
import orkhoian.aleksei.tasklist.mapper.TaskMapper;
import orkhoian.aleksei.tasklist.service.TaskService;

import java.util.Map;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(TaskController.class)
@Import(TestSecurityConfig.class)
public class TaskControllerTest {

    @MockBean
    private TaskService taskService;

    @MockBean
    private TaskMapper taskMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final ClassPathResource imageFile = new ClassPathResource("/image/cat.jpeg");

    @Test
    @DisplayName("Get by id successfully")
    void getById() throws Exception {
        Long taskId = 1L;
        Task task = Task.builder().id(taskId).build();
        TaskDto taskDto = TaskDto.builder().id(taskId).build();

        when(taskService.getById(taskId)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDto);

        mockMvc.perform(get("/api/v1/tasks/{id}", taskId))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(taskDto)));
    }

    @Test
    @DisplayName("Get by id failed with not found exception")
    void getByIdFail() throws Exception {
        Long taskId = 1L;
        String exceptionMessage = "Received response from Nulab: Not Found";
        ExceptionBody response = new ExceptionBody("404 NOT_FOUND \"%s\"".formatted(exceptionMessage));

        when(taskService.getById(taskId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, exceptionMessage));

        mockMvc.perform(get("/api/v1/tasks/{id}", taskId))
            .andExpect(status().isNotFound())
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Upload image successfully")
    void uploadImage() throws Exception {
        long id = 1L;
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "cat.jpeg",
            MediaType.IMAGE_JPEG_VALUE,
            imageFile.getInputStream()
        );

        doNothing().when(taskService).uploadImage(id, file);

        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/tasks/{id}/image", id)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
            )
            .andExpect(status().isOk());

        verify(taskService).uploadImage(id, file);
    }

    @Test
    @DisplayName("Upload image failed because file not provided")
    void uploadImageFail() throws Exception {
        mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/tasks/{id}/image", 1L)
                .contentType(MediaType.MULTIPART_FORM_DATA)
            )
            .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(
                    new ExceptionBody("Required part 'file' is not present."))));
    }

    @Test
    @DisplayName("Update task successfully")
    void update() throws Exception {
        TaskDto taskDto = TaskDto.builder().id(1L).title("title").build();
        Task task = new Task();

        when(taskMapper.toEntity(taskDto)).thenReturn(task);
        when(taskService.update(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskDto);

        mockMvc.perform(put("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDto)))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(taskDto)));
    }

    @Test
    @DisplayName("Update task failed with validation exception")
    void updateFail() throws Exception {
        TaskDto taskDto = new TaskDto();

        mockMvc.perform(put("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().json(objectMapper.writeValueAsString(
                new ExceptionBody("Validation failed",
                    Map.of(
                        "id", "must not be null",
                        "title", "must not be blank"
                    )))));
    }

    @Test
    @DisplayName("Update task failed due task not found")
    void updateFail2() throws Exception {
        TaskDto taskDto = TaskDto.builder().id(1L).title("title").build();
        Task task = new Task();
        String exceptionMessage = "Task not found";

        when(taskMapper.toEntity(taskDto)).thenReturn(task);
        when(taskService.update(task)).thenThrow(new ResourceNotFoundException(exceptionMessage));

        mockMvc.perform(put("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDto)))
            .andExpect(status().isNotFound())
            .andExpect(content().json(objectMapper.writeValueAsString(new ExceptionBody(exceptionMessage))));
    }

    @Test
    @DisplayName("Delete by id successfully")
    void deleteById() throws Exception {
        long id = 1L;

        doNothing().when(taskService).delete(id);

        mockMvc.perform(delete("/api/v1/tasks/{id}", id))
            .andExpect(status().isOk());

        verify(taskService).delete(id);
    }
}
