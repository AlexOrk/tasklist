package orkhoian.aleksei.tasklist.dto.nulab;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriBuilder;
import orkhoian.aleksei.tasklist.utils.Utils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueAddParamsDto {

    public static final String PROJECT_ID_PARAM = "projectId";
    public static final String SUMMARY_PARAM = "summary";
    public static final String ISSUE_TYPE_ID_PARAM = "issueTypeId";
    public static final String PRIORITY_ID_PARAM = "priorityId";
    public static final String DESCRIPTION_PARAM = "description";
    public static final String DUE_DATE_PARAM = "dueDate";

    @NotNull
    private Long projectId;
    @NotBlank
    private String summary;
    @NotNull
    private Long issueTypeId;
    @NotNull
    private Long priorityId;
    private String description;
    private String dueDate;

    public void applyParams(UriBuilder uriBuilder) {
        uriBuilder.queryParam(PROJECT_ID_PARAM, projectId);
        uriBuilder.queryParam(SUMMARY_PARAM, summary);
        uriBuilder.queryParam(ISSUE_TYPE_ID_PARAM, issueTypeId);
        uriBuilder.queryParam(PRIORITY_ID_PARAM, priorityId);

        if (description != null) {
            uriBuilder.queryParam(DESCRIPTION_PARAM, description);
        }

        if (dueDate != null) {
            Utils.checkFutureDate(dueDate);
            uriBuilder.queryParam(DUE_DATE_PARAM, dueDate);
        }
    }
}
