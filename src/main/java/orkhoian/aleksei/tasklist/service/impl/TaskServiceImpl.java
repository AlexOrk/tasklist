package orkhoian.aleksei.tasklist.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import orkhoian.aleksei.tasklist.domain.exception.ResourceNotFoundException;
import orkhoian.aleksei.tasklist.domain.task.Status;
import orkhoian.aleksei.tasklist.domain.task.Task;
import orkhoian.aleksei.tasklist.repository.TaskRepository;
import orkhoian.aleksei.tasklist.service.ImageService;
import orkhoian.aleksei.tasklist.service.TaskService;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ImageService imageService;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, ImageService imageService) {
        this.taskRepository = taskRepository;
        this.imageService = imageService;
    }

    @Override
    @Cacheable(value = "TaskService::getById", key = "#id")
    public Task getById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    @Override
    public List<Task> getAllByUserId(Long userId) {
        return taskRepository.findAllByUserId(userId);
    }

    @Override
    @Transactional
    @CachePut(value = "TaskService::getById", key = "#task.id")
    public Task update(Task task) {
        Task taskFromDb = taskRepository.findById(task.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        taskFromDb.setStatus(task.getStatus() == null ? Status.TODO : task.getStatus());
        taskFromDb.setTitle(task.getTitle());
        taskFromDb.setDescription(task.getDescription());
        taskFromDb.setExpirationDate(task.getExpirationDate());
        taskRepository.save(task);
        return task;
    }

    @Override
    @Transactional
    @Cacheable(value = "TaskService::create", condition = "#task.id!=null", key = "#task.id")
    public Task create(Task task, Long userId) {
        if (task.getStatus() == null) {
            task.setStatus(Status.TODO);
        }
        taskRepository.save(task);
        taskRepository.assignTask(userId, task.getId());
        return task;
    }

    @Override
    @Transactional
    @CacheEvict(value = "TaskService::getById", key = "#taskId")
    public void uploadImage(Long taskId, MultipartFile file) {
        String fileName = imageService.upload(file);
        taskRepository.addImage(taskId, fileName);
    }

    @Override
    @Transactional
    @CacheEvict(value = "TaskService::getById", key = "#id")
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    @Cacheable(value = "TaskService::isTaskExists", key = "#id")
    public boolean isTaskExists(Long id) {
        return taskRepository.existsById(id);
    }
}
