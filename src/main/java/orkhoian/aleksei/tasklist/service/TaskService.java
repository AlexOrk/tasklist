package orkhoian.aleksei.tasklist.service;

import orkhoian.aleksei.tasklist.domain.task.Task;

import java.util.List;

public interface TaskService {

    Task getById(Long id);

    List<Task> getAllByUserId(Long userId);

    Task create(Task task, Long userId);

    Task update(Task task);

    void delete(Long id);
}
