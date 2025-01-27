package orkhoian.aleksei.tasklist.dto.nulab;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

@Data
public class IssueDto {

    private long id;
    private long projectId;
    private String issueKey;
    private long keyId;
    private IssueType issueType;
    private String summary;
    private String description;
    private Status status;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime created;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime dueDate;

    @Data
    public static class IssueType {
        private long id;
        private String name;
    }

    @Data
    public static class Status {
        private long id;
        private String name;
    }
}
