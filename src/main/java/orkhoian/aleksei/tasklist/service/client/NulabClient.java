package orkhoian.aleksei.tasklist.service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import orkhoian.aleksei.tasklist.dto.nulab.IssueAddParamsDto;
import orkhoian.aleksei.tasklist.dto.nulab.IssueDto;
import orkhoian.aleksei.tasklist.dto.nulab.IssueGetParamsDto;
import orkhoian.aleksei.tasklist.dto.nulab.ProjectDto;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class NulabClient {

    public static final String PROJECTS_PATH = "/projects";
    public static final String ISSUES_PATH = "/issues";
    public static final String APIKEY_PARAM = "apiKey";

    private final RestClient client;

    @Autowired
    public NulabClient(@Value("${client.nulab-uri}") String uriBase) {
        this.client = RestClient.create(uriBase);
    }

    public List<ProjectDto> getProjectList(String apiKey) {

        return client.get()
            .uri(uriBuilder -> uriBuilder
                .path(PROJECTS_PATH)
                .queryParam(APIKEY_PARAM, apiKey)
                .build()
            )
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                NulabClient::handleExceptionResponse
            )
            .body(new ParameterizedTypeReference<>() {});
    }

    public List<IssueDto> getIssueList(String apiKey, IssueGetParamsDto params) {

        return client.get()
            .uri(uriBuilder -> {
                params.applyParams(uriBuilder);
                return uriBuilder
                    .path(ISSUES_PATH)
                    .queryParam(APIKEY_PARAM, apiKey)
                    .build();
                }
            )
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                NulabClient::handleExceptionResponse
            )
            .body(new ParameterizedTypeReference<>() {});
    }

    public IssueDto getIssue(String apiKey, String issueIdOrKey) {

        return client.get()
            .uri(uriBuilder -> uriBuilder
                .path("%s/%s".formatted(ISSUES_PATH, issueIdOrKey))
                .queryParam(APIKEY_PARAM, apiKey)
                .build()
            )
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                NulabClient::handleExceptionResponse
            )
            .body(new ParameterizedTypeReference<>() {});
    }

    @Retryable(retryFor = ResponseStatusException.class, backoff = @Backoff(delay = 3000))
    public IssueDto addIssue(String apiKey, IssueAddParamsDto params) {

        return client.post()
            .uri(uriBuilder -> {
                    params.applyParams(uriBuilder);
                    return uriBuilder
                        .path(ISSUES_PATH)
                        .queryParam(APIKEY_PARAM, apiKey)
                        .build();
                }
            )
            .retrieve()
            .onStatus(
                HttpStatusCode::isError,
                NulabClient::handleExceptionResponse
            )
            .body(new ParameterizedTypeReference<>() {});
    }

    private static void handleExceptionResponse(HttpRequest request, ClientHttpResponse response) throws IOException {
        throw new ResponseStatusException(response.getStatusCode(),
            "Received response from Nulab: " + response.getStatusText());
    }
}
