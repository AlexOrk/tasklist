package orkhoian.aleksei.tasklist.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import orkhoian.aleksei.tasklist.controller.AsyncExceptionHandler;
import orkhoian.aleksei.tasklist.utils.ExcludeFromJacocoGeneratedReport;

@Configuration
@ExcludeFromJacocoGeneratedReport
public class AsyncConfig implements AsyncConfigurer {

    private final AsyncExceptionHandler asyncExceptionHandler;

    @Autowired
    public AsyncConfig(AsyncExceptionHandler asyncExceptionHandler) {
        this.asyncExceptionHandler = asyncExceptionHandler;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return asyncExceptionHandler;
    }
}
