package orkhoian.aleksei.tasklist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;
import orkhoian.aleksei.tasklist.config.TestSecurityConfig;
import orkhoian.aleksei.tasklist.config.WithMockJwtUser;
import orkhoian.aleksei.tasklist.domain.exception.ExceptionBody;
import orkhoian.aleksei.tasklist.domain.user.User;
import orkhoian.aleksei.tasklist.dto.nulab.IssueAddParamsDto;
import orkhoian.aleksei.tasklist.dto.nulab.IssueDto;
import orkhoian.aleksei.tasklist.dto.nulab.IssueGetParamsDto;
import orkhoian.aleksei.tasklist.dto.nulab.ProjectDto;
import orkhoian.aleksei.tasklist.service.NulabService;
import orkhoian.aleksei.tasklist.service.UserService;
import orkhoian.aleksei.tasklist.service.impl.NulabServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NulabController.class)
@Import(TestSecurityConfig.class)
public class NulabControllerTest {

    @MockBean
    private NulabService nulabService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String userApiKey;

    @BeforeEach
    void setUp() {
        userApiKey = "test-api-key";

        User user = User.builder()
            .id(1L)
            .name("Test User")
            .username("test@example.com")
            .password("password123")
            .apiKey(userApiKey)
            .build();

        when(userService.getById(user.getId())).thenReturn(user);
    }

    @Test
    @DisplayName("Get projects successfully")
    @WithMockJwtUser
    void getProjectList() throws Exception {
        List<ProjectDto> projects = List.of(
            ProjectDto.builder().id(1L).projectKey("1").name("Project 1").build(),
            ProjectDto.builder().id(2L).projectKey("2").name("Project 2").build());

        when(nulabService.getProjectList(userApiKey)).thenReturn(projects);

        mockMvc.perform(get("/api/v1/nulab/projects"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(projects)));
    }

    @Test
    @DisplayName("Get project list failed due to user not authorised")
    void getProjectListFail() throws Exception {
        ExceptionBody response = new ExceptionBody("User not authorised!");

        mockMvc.perform(get("/api/v1/nulab/projects"))
            .andExpect(status().isUnauthorized())
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Get issues successfully")
    @WithMockJwtUser
    void getIssueList() throws Exception {
        List<IssueDto> issues = List.of(
            IssueDto.builder().id(1L).projectId(1L).issueKey("1").summary("Issue 1").build(),
            IssueDto.builder().id(2L).projectId(2L).issueKey("2").summary("Issue 2").build());

        IssueGetParamsDto params = IssueGetParamsDto.builder()
            .projectId(List.of(1L, 2L))
            .issueTypeId(List.of(NulabServiceImpl.DEFAULT_ISSUE_TYPE_ID))
            .statusId(List.of(1L))
            .build();

        when(nulabService.getIssueList(userApiKey, params)).thenReturn(issues);

        mockMvc.perform(get("/api/v1/nulab/issues")
                .param(IssueGetParamsDto.PROJECT_ID_PARAM, "1,2")
                .param(IssueGetParamsDto.ISSUE_TYPE_ID_PARAM, String.valueOf(NulabServiceImpl.DEFAULT_ISSUE_TYPE_ID))
                .param(IssueGetParamsDto.STATUS_ID_PARAM, "1")
            )
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(issues)));
    }

    @Test
    @DisplayName("Get issue list failed due to user not authorised")
    void getIssueListFail() throws Exception {
        ExceptionBody response = new ExceptionBody("User not authorised!");

        mockMvc.perform(get("/api/v1/nulab/issues"))
            .andExpect(status().isUnauthorized())
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Add issue successfully")
    @WithMockJwtUser
    void getIssue() throws Exception {
        IssueDto issue = IssueDto.builder().id(1L).projectId(1L).issueKey("1").summary("Issue 1").build();
        String issueIdOrKey = "1";

        when(nulabService.getIssue(userApiKey, issueIdOrKey)).thenReturn(issue);

        mockMvc.perform(get("/api/v1/nulab/issues/{issueIdOrKey}", issueIdOrKey))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(issue)));
    }

    @Test
    @DisplayName("Get issue failed due to user not authorised")
    void getIssueFail() throws Exception {
        ExceptionBody response = new ExceptionBody("User not authorised!");

        mockMvc.perform(get("/api/v1/nulab/issues/{issueIdOrKey}", "1"))
            .andExpect(status().isUnauthorized())
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Get issue failed due because issue not found")
    @WithMockJwtUser
    void getIssueFail2() throws Exception {
        String exceptionMessage = "Received response from Nulab: Not Found";
        ExceptionBody response = new ExceptionBody("404 NOT_FOUND \"%s\"".formatted(exceptionMessage));
        String issueIdOrKey = "1";

        when(nulabService.getIssue(userApiKey, issueIdOrKey))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, exceptionMessage));

        mockMvc.perform(get("/api/v1/nulab/issues/{issueIdOrKey}", issueIdOrKey))
            .andExpect(status().isNotFound())
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @DisplayName("Add issue successfully")
    @WithMockJwtUser
    void addIssue() throws Exception {
        IssueAddParamsDto params = IssueAddParamsDto.builder()
            .projectId(1L)
            .summary("Issue 1")
            .issueTypeId(NulabServiceImpl.DEFAULT_ISSUE_TYPE_ID)
            .priorityId(NulabServiceImpl.DEFAULT_PRIORITY_ID)
            .build();
        IssueDto issue = new IssueDto();

        when(nulabService.addIssue(userApiKey, params)).thenReturn(issue);

        mockMvc.perform(post("/api/v1/nulab/issues")
                .contentType(MediaType.APPLICATION_JSON)
                .param(IssueAddParamsDto.PROJECT_ID_PARAM, params.getProjectId().toString())
                .param(IssueAddParamsDto.SUMMARY_PARAM, params.getSummary())
                .param(IssueAddParamsDto.ISSUE_TYPE_ID_PARAM, params.getIssueTypeId().toString())
                .param(IssueAddParamsDto.PRIORITY_ID_PARAM, params.getPriorityId().toString())
            )
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(issue)));
    }

    @Test
    @DisplayName("Add issue failed due to user not authorised")
    void addIssueFail() throws Exception {
        ExceptionBody response = new ExceptionBody("User not authorised!");

        mockMvc.perform(post("/api/v1/nulab/issues")
                .contentType(MediaType.APPLICATION_JSON)
                .param(IssueAddParamsDto.PROJECT_ID_PARAM, "1")
                .param(IssueAddParamsDto.SUMMARY_PARAM, "Issue 1")
                .param(IssueAddParamsDto.ISSUE_TYPE_ID_PARAM, String.valueOf(NulabServiceImpl.DEFAULT_ISSUE_TYPE_ID))
                .param(IssueAddParamsDto.PRIORITY_ID_PARAM, String.valueOf(NulabServiceImpl.DEFAULT_PRIORITY_ID))
            )
            .andExpect(status().isUnauthorized())
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @ParameterizedTest
    @DisplayName("Add issue validation failed")
    @MethodSource("provideDataForNulabValidationFail")
    @WithMockJwtUser
    void addIssueValidationFail(IssueAddParamsDto params, Map<String, String> errors) throws Exception {
        ExceptionBody response = new ExceptionBody("Validation failed", errors);

        MockHttpServletRequestBuilder requestBuilder = post("/api/v1/nulab/issues")
                .contentType(MediaType.APPLICATION_JSON);

        if (params.getProjectId() != null) {
            requestBuilder.param(IssueAddParamsDto.PROJECT_ID_PARAM, params.getProjectId().toString());
        }
        if (params.getSummary() != null) {
            requestBuilder.param(IssueAddParamsDto.SUMMARY_PARAM, params.getSummary());
        }
        if (params.getIssueTypeId() != null) {
            requestBuilder.param(IssueAddParamsDto.ISSUE_TYPE_ID_PARAM, params.getIssueTypeId().toString());
        }
        if (params.getPriorityId() != null) {
            requestBuilder.param(IssueAddParamsDto.PRIORITY_ID_PARAM, params.getPriorityId().toString());
        }

        mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    static Stream<Arguments> provideDataForNulabValidationFail() {
        return Stream.of(
            Arguments.of(
                IssueAddParamsDto.builder()
                    .projectId(null)
                    .summary("Issue 1")
                    .issueTypeId(NulabServiceImpl.DEFAULT_ISSUE_TYPE_ID)
                    .priorityId(NulabServiceImpl.DEFAULT_PRIORITY_ID)
                    .build(),
                Map.of(IssueAddParamsDto.PROJECT_ID_PARAM, "must not be null")
            ),
            Arguments.of(
                IssueAddParamsDto.builder()
                    .projectId(1L)
                    .summary(null)
                    .issueTypeId(NulabServiceImpl.DEFAULT_ISSUE_TYPE_ID)
                    .priorityId(NulabServiceImpl.DEFAULT_PRIORITY_ID)
                    .build(),
                Map.of(IssueAddParamsDto.SUMMARY_PARAM, "must not be blank")
            ),
            Arguments.of(
                IssueAddParamsDto.builder()
                    .projectId(1L)
                    .summary(" ")
                    .issueTypeId(NulabServiceImpl.DEFAULT_ISSUE_TYPE_ID)
                    .priorityId(NulabServiceImpl.DEFAULT_PRIORITY_ID)
                    .build(),
                Map.of(IssueAddParamsDto.SUMMARY_PARAM, "must not be blank")
            ),
            Arguments.of(
                IssueAddParamsDto.builder()
                    .projectId(1L)
                    .summary("Issue 1")
                    .issueTypeId(null)
                    .priorityId(NulabServiceImpl.DEFAULT_PRIORITY_ID)
                    .build(),
                Map.of(IssueAddParamsDto.ISSUE_TYPE_ID_PARAM, "must not be null")
            ),
            Arguments.of(
                IssueAddParamsDto.builder()
                    .projectId(1L)
                    .summary("Issue 1")
                    .issueTypeId(NulabServiceImpl.DEFAULT_ISSUE_TYPE_ID)
                    .priorityId(null)
                    .build(),
                Map.of(IssueAddParamsDto.PRIORITY_ID_PARAM, "must not be null")
            )
        );
    }
}
