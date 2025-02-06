package orkhoian.aleksei.tasklist.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import orkhoian.aleksei.tasklist.dto.task.TaskDto;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AsyncExceptionHandlerTest {

    @Mock
    private Appender<ILoggingEvent> mockAppender;

    @InjectMocks
    private AsyncExceptionHandler asyncExceptionHandler;

    @BeforeEach
    void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(AsyncExceptionHandler.class);
        logger.setLevel(Level.ERROR);
        logger.addAppender(mockAppender);
    }

    @Test
    @DisplayName("Handle uncaught exception from async task and log it")
    void handleUncaughtException() throws NoSuchMethodException {
        String exceptionMessage = "Test exception";
        String title = "title";
        String expectedLogMessage = "%s, task name: %s".formatted(exceptionMessage, title);
        Throwable exception = new RuntimeException(exceptionMessage);
        Method mockMethod = AsyncExceptionHandlerTest.class.getDeclaredMethod("dummyMethod");
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle(title);

        asyncExceptionHandler.handleUncaughtException(exception, mockMethod, taskDto);

        ArgumentCaptor<ILoggingEvent> captor = ArgumentCaptor.forClass(ILoggingEvent.class);
        verify(mockAppender, times(1)).doAppend(captor.capture());

        ILoggingEvent loggedEvent = captor.getValue();
        String actualLogMessage = loggedEvent.getFormattedMessage();
        assertEquals(expectedLogMessage, actualLogMessage);
    }

    private void dummyMethod() {
    }

}
