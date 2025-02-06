package orkhoian.aleksei.tasklist.dto.nulab;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IssueAddParamsDtoTest {

    @Test
    @DisplayName("applyParams adds all required params to UriBuilder successfully")
    void applyParams() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String futureDate = tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        IssueAddParamsDto issueAddParamsDto = IssueAddParamsDto.builder()
                .projectId(1L)
                .summary("summary")
                .issueTypeId(1L)
                .priorityId(1L)
                .description("description")
                .dueDate(futureDate)
                .build();

        UriBuilder uriBuilder = UriComponentsBuilder.newInstance();

        issueAddParamsDto.applyParams(uriBuilder);
        String query = uriBuilder.build().getQuery();

        assertTrue(query.contains(IssueAddParamsDto.PROJECT_ID_PARAM + "=1"));
        assertTrue(query.contains(IssueAddParamsDto.SUMMARY_PARAM + "=summary"));
        assertTrue(query.contains(IssueAddParamsDto.ISSUE_TYPE_ID_PARAM + "=1"));
        assertTrue(query.contains(IssueAddParamsDto.PRIORITY_ID_PARAM + "=1"));
        assertTrue(query.contains(IssueAddParamsDto.DESCRIPTION_PARAM + "=description"));
        assertTrue(query.contains(IssueAddParamsDto.DUE_DATE_PARAM + "=%s".formatted(futureDate)));
    }

    @Test
    @DisplayName("applyParams throws IllegalArgumentException when dueDate is invalid")
    void applyParamsFail() {
        IssueAddParamsDto issueAddParamsDto = IssueAddParamsDto.builder()
            .dueDate("invalid date")
            .build();

        UriBuilder uriBuilder = UriComponentsBuilder.newInstance();

        assertThrows(IllegalArgumentException.class, () -> issueAddParamsDto.applyParams(uriBuilder),
            "Invalid date format. Expected yyyy-MM-dd, but was " + issueAddParamsDto.getDueDate());
    }

    @Test
    @DisplayName("applyParams throws IllegalArgumentException when dueDate is in the past")
    void applyParamsFail2() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String previousDate = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        IssueAddParamsDto issueAddParamsDto = IssueAddParamsDto.builder()
            .dueDate(previousDate)
            .build();

        UriBuilder uriBuilder = UriComponentsBuilder.newInstance();

        assertThrows(IllegalArgumentException.class, () -> issueAddParamsDto.applyParams(uriBuilder),
            "Invalid date. The date entered must be later than now, but was " + issueAddParamsDto.getDueDate());
    }
}
