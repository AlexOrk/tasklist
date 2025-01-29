package orkhoian.aleksei.tasklist.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import orkhoian.aleksei.tasklist.service.NulabService;
import orkhoian.aleksei.tasklist.service.client.NulabClient;
import orkhoian.aleksei.tasklist.dto.nulab.IssueAddParamsDto;
import orkhoian.aleksei.tasklist.dto.nulab.IssueDto;
import orkhoian.aleksei.tasklist.dto.nulab.IssueGetParamsDto;
import orkhoian.aleksei.tasklist.dto.nulab.ProjectDto;
import orkhoian.aleksei.tasklist.dto.nulab.TaskPublishParamsDto;
import orkhoian.aleksei.tasklist.dto.task.TaskDto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class NulabServiceImpl implements NulabService {

    public static final long DEFAULT_ISSUE_TYPE_ID = 614093L;
    public static final long DEFAULT_PRIORITY_ID = 3L;

    private final NulabClient nulabClient;

    @Autowired
    public NulabServiceImpl(NulabClient nulabClient) {
        this.nulabClient = nulabClient;
    }

    @Override
    public List<ProjectDto> getProjectList(String apiKey) {
        return nulabClient.getProjectList(apiKey);
    }

    @Override
    public List<IssueDto> getIssueList(String apiKey, IssueGetParamsDto params) {
        return nulabClient.getIssueList(apiKey, params);
    }

    @Override
    public IssueDto getIssue(String apiKey, String issueIdOrKey) {
        return nulabClient.getIssue(apiKey, issueIdOrKey);
    }

    @Override
    public IssueDto addIssue(String apiKey, IssueAddParamsDto params) {
        return nulabClient.addIssue(apiKey, params);
    }

    @Override
    @Async
    public void publishTaskInNulab(String apiKey, TaskPublishParamsDto taskParams, TaskDto taskDto) {
        IssueAddParamsDto issueParams = IssueAddParamsDto.builder()
            .projectId(taskParams.getProjectId())
            .issueTypeId(Objects.requireNonNullElse(taskParams.getIssueTypeId(), DEFAULT_ISSUE_TYPE_ID))
            .priorityId(Objects.requireNonNullElse(taskParams.getPriorityId(), DEFAULT_PRIORITY_ID))
            .summary(taskDto.getTitle())
            .build();

        if (taskDto.getDescription() != null) {
            issueParams.setDescription(taskDto.getDescription());
        }

        if (taskDto.getExpirationDate() != null) {
            issueParams.setDueDate(taskDto.getExpirationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        nulabClient.addIssue(apiKey, issueParams);
    }
}
