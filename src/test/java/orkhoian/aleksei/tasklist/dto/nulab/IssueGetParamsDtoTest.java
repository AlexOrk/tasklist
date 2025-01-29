package orkhoian.aleksei.tasklist.dto.nulab;

import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class IssueGetParamsDtoTest {

    @Test
    void testApplyParams() {
        IssueGetParamsDto issueGetParamsDto = IssueGetParamsDto.builder()
                .projectId(List.of(1L, 2L))
                .issueTypeId(List.of(1L, 2L))
                .statusId(List.of(1L, 2L))
                .id(List.of(1L, 2L))
                .build();

        UriBuilder uriBuilder = UriComponentsBuilder.newInstance();

        issueGetParamsDto.applyParams(uriBuilder);
        String query = uriBuilder.build().getQuery();

        assertTrue(query.contains(IssueGetParamsDto.PROJECT_ID_PARAM + "=1"));
        assertTrue(query.contains(IssueGetParamsDto.PROJECT_ID_PARAM + "=2"));
        assertTrue(query.contains(IssueGetParamsDto.ISSUE_TYPE_ID_PARAM + "=1"));
        assertTrue(query.contains(IssueGetParamsDto.ISSUE_TYPE_ID_PARAM + "=2"));
        assertTrue(query.contains(IssueGetParamsDto.STATUS_ID_PARAM + "=1"));
        assertTrue(query.contains(IssueGetParamsDto.STATUS_ID_PARAM + "=2"));
        assertTrue(query.contains(IssueGetParamsDto.ID_PARAM + "=1"));
        assertTrue(query.contains(IssueGetParamsDto.ID_PARAM + "=2"));
    }
}
