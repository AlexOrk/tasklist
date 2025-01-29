package orkhoian.aleksei.tasklist.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import orkhoian.aleksei.tasklist.domain.exception.ResourceNotFoundException;
import orkhoian.aleksei.tasklist.domain.task.Status;
import orkhoian.aleksei.tasklist.domain.task.Task;
import orkhoian.aleksei.tasklist.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private final Task expectedTask = new Task();
    private final long id = 1L;

    @Test
    @DisplayName("Get task by id successfully")
    void getById() {
        expectedTask.setId(id);

        when(taskRepository.findById(id)).thenReturn(Optional.of(expectedTask));

        Task actual = taskService.getById(id);

        assertEquals(expectedTask, actual);
        verify(taskRepository).findById(id);
    }

    @Test
    @DisplayName("Get task by id failed")
    void getByIdNotFound() {
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getById(id));
        verify(taskRepository).findById(id);
    }

    @Test
    @DisplayName("Get all tasks by user id successfully")
    void getAllByUserId() {
        List<Task> expected = List.of(new Task(), new Task());

        when(taskRepository.findAllByUserId(id)).thenReturn(expected);

        List<Task> actual = taskService.getAllByUserId(id);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Update task successfully")
    void update() {
        expectedTask.setId(id);
        expectedTask.setTitle("title");
        expectedTask.setDescription("description");
        expectedTask.setExpirationDate(LocalDateTime.now());
        expectedTask.setStatus(Status.DONE);

        Task actual = taskService.update(expectedTask);

        assertEquals(expectedTask, actual);
        verify(taskRepository).save(expectedTask);
    }

    @Test
    @DisplayName("Update task with null status")
    void updateNullStatus() {
        Status expectedStatus = Status.TODO;
        expectedTask.setExpirationDate(LocalDateTime.now());

        Task actual = taskService.update(expectedTask);

        assertEquals(expectedStatus, actual.getStatus());
        verify(taskRepository).save(expectedTask);
    }

    @Test
    @DisplayName("Create task successfully")
    void create() {
        Long expectedId = 1L;

        doAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId(id);
            return task;
        }).when(taskRepository).save(expectedTask);

        Task actual = taskService.create(expectedTask, expectedId);

        assertEquals(expectedId, actual.getId());
        verify(taskRepository).save(expectedTask);
    }

    @Test
    @DisplayName("Delete task successfully")
    void delete() {
        taskService.delete(id);
        verify(taskRepository).deleteById(id);
    }
}
