package orkhoian.aleksei.tasklist.dto.nulab;

import lombok.Data;
import org.springframework.web.util.UriBuilder;

import java.util.List;

@Data
public class IssueGetParamsDto {

    public static final String PROJECT_ID_PARAM = "projectId[]";
    public static final String ISSUE_TYPE_ID_PARAM = "issueTypeId[]";
    public static final String STATUS_ID_PARAM = "statusId[]";
    public static final String ID_PARAM = "id[]";

    private List<Long> projectId;
    private List<Long> issueTypeId;
    private List<Long> statusId;
    private List<Long> id;

    public void applyParams(UriBuilder uriBuilder) {

        if (projectId != null && !projectId.isEmpty()) {
            projectId.forEach(pId -> uriBuilder.queryParam(PROJECT_ID_PARAM, pId));
        }

        if (issueTypeId != null && !issueTypeId.isEmpty()) {
            issueTypeId.forEach(iTypeId -> uriBuilder.queryParam(ISSUE_TYPE_ID_PARAM, iTypeId));
        }

        if (statusId != null && !statusId.isEmpty()) {
            statusId.forEach(sId -> uriBuilder.queryParam(STATUS_ID_PARAM, sId));
        }

        if (id != null && !id.isEmpty()) {
            id.forEach(iId -> uriBuilder.queryParam(ID_PARAM, iId));
        }
    }
}
