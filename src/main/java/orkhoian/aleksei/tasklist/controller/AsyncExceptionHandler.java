package orkhoian.aleksei.tasklist.controller;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;
import orkhoian.aleksei.tasklist.dto.task.TaskDto;

import java.lang.reflect.Method;

@Component
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(AsyncExceptionHandler.class);

    @Override
    public void handleUncaughtException(@NotNull Throwable throwable, @NotNull Method method, @NotNull Object... params) {
        String title = null;
        for (Object param : params) {
            if (param instanceof TaskDto taskDto) {
                title = taskDto.getTitle();
            }
        }
        log.error("{}, task name: {}", throwable.getMessage(), title);
    }
}
