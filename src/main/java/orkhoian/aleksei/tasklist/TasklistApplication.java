package orkhoian.aleksei.tasklist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import orkhoian.aleksei.tasklist.utils.ExcludeFromJacocoGeneratedReport;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableRetry
@ExcludeFromJacocoGeneratedReport
public class TasklistApplication {

    public static void main(String[] args) {
        SpringApplication.run(TasklistApplication.class, args);
    }

}
