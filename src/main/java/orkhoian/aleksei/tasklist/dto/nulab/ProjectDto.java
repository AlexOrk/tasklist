package orkhoian.aleksei.tasklist.dto.nulab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {

    private long id;
    private String projectKey;
    private String name;
}
