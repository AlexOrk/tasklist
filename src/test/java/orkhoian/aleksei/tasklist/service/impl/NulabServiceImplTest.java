package orkhoian.aleksei.tasklist.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import orkhoian.aleksei.tasklist.dto.nulab.IssueAddParamsDto;
import orkhoian.aleksei.tasklist.dto.nulab.IssueDto;
import orkhoian.aleksei.tasklist.dto.nulab.IssueGetParamsDto;
import orkhoian.aleksei.tasklist.dto.nulab.ProjectDto;
import orkhoian.aleksei.tasklist.dto.nulab.TaskPublishParamsDto;
import orkhoian.aleksei.tasklist.dto.task.TaskDto;
import orkhoian.aleksei.tasklist.service.client.NulabClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NulabServiceImplTest {

    @Mock
    private NulabClient nulabClient;

    @InjectMocks
    private NulabServiceImpl nulabService;

    private final String apiKey = "apiKey";

    @Test
    @DisplayName("Get project list from Nulab API successfully")
    void getProjectList() {
        List<ProjectDto> expected = List.of(new ProjectDto(), new ProjectDto());

        when(nulabClient.getProjectList(apiKey)).thenReturn(expected);

        List<ProjectDto> actual = nulabService.getProjectList(apiKey);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get an empty project list from Nulab API successfully")
    void getProjectListEmpty() {
        when(nulabClient.getProjectList(apiKey)).thenReturn(new ArrayList<>());

        assertTrue(() -> nulabService.getProjectList(apiKey).isEmpty());
    }

    @Test
    @DisplayName("Get project list from Nulab API failed")
    void getProjectListFail() {
        when(nulabClient.getProjectList(apiKey))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));

        assertThrows(ResponseStatusException.class, () -> nulabService.getProjectList(apiKey));
    }

    @Test
    @DisplayName("Get issue list from Nulab API successfully")
    void getIssueList() {
        IssueGetParamsDto params = new IssueGetParamsDto();
        List<IssueDto> expected = List.of(new IssueDto(), new IssueDto());

        when(nulabClient.getIssueList(apiKey, params)).thenReturn(expected);

        List<IssueDto> actual = nulabService.getIssueList(apiKey, params);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get an empty issue list from Nulab API successfully")
    void getIssueListEmpty() {
        IssueGetParamsDto params = new IssueGetParamsDto();

        when(nulabClient.getIssueList(apiKey, params)).thenReturn(new ArrayList<>());

        assertTrue(() -> nulabService.getIssueList(apiKey, params).isEmpty());
    }

    @Test
    @DisplayName("Get issue list from Nulab API failed")
    void getIssueListFail() {
        IssueGetParamsDto params = new IssueGetParamsDto();

        when(nulabClient.getIssueList(apiKey, params))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));

        assertThrows(ResponseStatusException.class, () -> nulabService.getIssueList(apiKey, params));
    }

    @Test
    @DisplayName("Get issue from Nulab API successfully")
    void getIssue() {
        String issueIdOrKey = "123";
        IssueDto expected = new IssueDto();

        when(nulabClient.getIssue(apiKey, issueIdOrKey)).thenReturn(expected);

        IssueDto actual = nulabService.getIssue(apiKey, issueIdOrKey);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get issue from Nulab API failed")
    void getIssueFail() {
        String issueIdOrKey = "123";

        when(nulabClient.getIssue(apiKey, issueIdOrKey))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));

        assertThrows(ResponseStatusException.class, () -> nulabService.getIssue(apiKey, issueIdOrKey));
    }

    @Test
    @DisplayName("Add issue to Nulab API successfully")
    void addIssue() {
        IssueAddParamsDto issue = new IssueAddParamsDto();
        IssueDto expected = new IssueDto();

        when(nulabClient.addIssue(apiKey, issue)).thenReturn(expected);

        IssueDto actual = nulabService.addIssue(apiKey, issue);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Add issue to Nulab API failed")
    void addIssueFail() {
        IssueAddParamsDto issue = new IssueAddParamsDto();

        when(nulabClient.addIssue(apiKey, issue))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));

        assertThrows(ResponseStatusException.class, () -> nulabService.addIssue(apiKey, issue));
    }

    @Test
    @DisplayName("Publish task in Nulab API successfully")
    void publishTaskInNulab() {
        TaskPublishParamsDto taskParams = TaskPublishParamsDto.builder()
            .projectId(1L)
            .issueTypeId(2L)
            .priorityId(3L)
            .build();

        TaskDto taskDto = TaskDto.builder()
            .title("title")
            .description("description")
            .expirationDate(LocalDateTime.now())
            .build();

        IssueAddParamsDto issueParams = IssueAddParamsDto.builder()
            .projectId(taskParams.getProjectId())
            .issueTypeId(taskParams.getIssueTypeId())
            .priorityId(taskParams.getPriorityId())
            .summary(taskDto.getTitle())
            .description(taskDto.getDescription())
            .dueDate(taskDto.getExpirationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            .build();

        when(nulabClient.addIssue(apiKey, issueParams)).thenReturn(new IssueDto());

        nulabService.publishTaskInNulab(apiKey, taskParams, taskDto);

        verify(nulabClient).addIssue(apiKey, issueParams);
    }

    @Test
    @DisplayName("Publish task in Nulab API failed")
    void publishTaskInNulabFail() {
        TaskPublishParamsDto taskParams = TaskPublishParamsDto.builder()
            .projectId(1L)
            .issueTypeId(2L)
            .priorityId(3L)
            .build();

        TaskDto taskDto = TaskDto.builder()
            .title("title")
            .build();

        IssueAddParamsDto issueParams = IssueAddParamsDto.builder()
            .projectId(taskParams.getProjectId())
            .issueTypeId(taskParams.getIssueTypeId())
            .priorityId(taskParams.getPriorityId())
            .summary(taskDto.getTitle())
            .build();

        when(nulabClient.addIssue(apiKey, issueParams))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));

        assertThrows(ResponseStatusException.class, () -> nulabService.publishTaskInNulab(apiKey, taskParams, taskDto));
    }
}
