package orkhoian.aleksei.tasklist.service.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import orkhoian.aleksei.tasklist.dto.nulab.IssueAddParamsDto;
import orkhoian.aleksei.tasklist.dto.nulab.IssueDto;
import orkhoian.aleksei.tasklist.dto.nulab.IssueGetParamsDto;
import orkhoian.aleksei.tasklist.dto.nulab.ProjectDto;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NulabClientTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec mockedRequestHeadersUriSpec;

    @Mock
    private RestClient.ResponseSpec mockedResponseSpec;

    @Mock
    private RestClient.RequestBodyUriSpec mockedRequestBodyUriSpec;

    private NulabClient nulabClient;

    private final String apiKey = "apiKey";
    private final String serverResponse = "Received response from Nulab: Server error";
    private final String exceptionMessage = "500 INTERNAL_SERVER_ERROR \"%s\"".formatted(serverResponse);

    @BeforeEach
    void setUp() {
        nulabClient = new NulabClient(restClient);
    }

    @Test
    @DisplayName("Get project list from Nulab API successfully")
    void getProjectList() {
        List<ProjectDto> projects = List.of(ProjectDto.builder().id(1L).build());
        getPreparations();
        when(mockedResponseSpec.onStatus(any(), any())).thenReturn(mockedResponseSpec);
        when(mockedResponseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(projects);
        assertEquals(projects, nulabClient.getProjectList(apiKey));
    }

    @Test
    @DisplayName("Get project list from Nulab API failed")
    void getProjectListFail() {
        getPreparations();
        when(mockedResponseSpec.onStatus(any(), any()))
            .thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, serverResponse));

        Exception exception = assertThrows(ResponseStatusException.class, () -> nulabClient.getProjectList(apiKey));
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Get issue list from Nulab API successfully")
    void getIssueList() {
        List<IssueDto> issues = List.of(IssueDto.builder().id(1L).build());
        getPreparations();
        when(mockedResponseSpec.onStatus(any(), any())).thenReturn(mockedResponseSpec);
        when(mockedResponseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(issues);
        assertEquals(issues, nulabClient.getIssueList(apiKey, new IssueGetParamsDto()));
    }

    @Test
    @DisplayName("Get issue list from Nulab API failed")
    void getIssueListFail() {
        getPreparations();
        when(mockedResponseSpec.onStatus(any(), any()))
            .thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, serverResponse));

        Exception exception =
            assertThrows(ResponseStatusException.class, () -> nulabClient.getIssueList(apiKey, new IssueGetParamsDto()));
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Get issue from Nulab API successfully")
    void getIssue() {
        IssueDto issue = IssueDto.builder().id(1L).build();
        getPreparations();
        when(mockedResponseSpec.onStatus(any(), any())).thenReturn(mockedResponseSpec);
        when(mockedResponseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(issue);
        assertEquals(issue, nulabClient.getIssue(apiKey, "issueId"));
    }

    @Test
    @DisplayName("Get issue from Nulab API failed")
    void getIssueFail() {
        getPreparations();
        when(mockedResponseSpec.onStatus(any(), any()))
            .thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, serverResponse));

        Exception exception =
            assertThrows(ResponseStatusException.class, () -> nulabClient.getIssue(apiKey, "issueId"));
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Add issue to Nulab API successfully")
    void addIssue() {
        IssueDto issue = IssueDto.builder().id(1L).build();

        when(restClient.post()).thenReturn(mockedRequestBodyUriSpec);
        when(mockedRequestBodyUriSpec.uri(any(Function.class))).thenReturn(mockedRequestBodyUriSpec);
        when(mockedRequestBodyUriSpec.retrieve()).thenReturn(mockedResponseSpec);
        when(mockedResponseSpec.onStatus(any(), any())).thenReturn(mockedResponseSpec);
        when(mockedResponseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(issue);

        assertEquals(issue, nulabClient.addIssue(apiKey, new IssueAddParamsDto()));
    }

    @Test
    @DisplayName("Add issue to Nulab API failed")
    void addIssueFail() {
        when(restClient.post()).thenReturn(mockedRequestBodyUriSpec);
        when(mockedRequestBodyUriSpec.uri(any(Function.class))).thenReturn(mockedRequestBodyUriSpec);
        when(mockedRequestBodyUriSpec.retrieve()).thenReturn(mockedResponseSpec);
        when(mockedResponseSpec.onStatus(any(), any()))
            .thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, serverResponse));

        Exception exception =
            assertThrows(ResponseStatusException.class, () -> nulabClient.addIssue(apiKey, new IssueAddParamsDto()));
        assertEquals(exceptionMessage, exception.getMessage());
    }

    private void getPreparations() {
        when(restClient.get()).thenReturn(mockedRequestHeadersUriSpec);
        when(mockedRequestHeadersUriSpec.uri(any(Function.class))).thenReturn(mockedRequestHeadersUriSpec);
        when(mockedRequestHeadersUriSpec.retrieve()).thenReturn(mockedResponseSpec);
    }
}
