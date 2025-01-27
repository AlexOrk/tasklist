package orkhoian.aleksei.tasklist.service;

import orkhoian.aleksei.tasklist.dto.nulab.IssueAddParamsDto;
import orkhoian.aleksei.tasklist.dto.nulab.IssueDto;
import orkhoian.aleksei.tasklist.dto.nulab.IssueGetParamsDto;
import orkhoian.aleksei.tasklist.dto.nulab.ProjectDto;
import orkhoian.aleksei.tasklist.dto.nulab.TaskPublishParamsDto;
import orkhoian.aleksei.tasklist.dto.task.TaskDto;

import java.util.List;

public interface NulabService {

    List<ProjectDto> getProjectList(String apiKey);

    List<IssueDto> getIssueList(String apiKey, IssueGetParamsDto params);

    IssueDto getIssue(String apiKey, String issueIdOrKey);

    IssueDto addIssue(String apiKey, IssueAddParamsDto params);

    void publishTaskInNulab(String apiKey, TaskPublishParamsDto params, TaskDto taskDto);
}
