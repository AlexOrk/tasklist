package orkhoian.aleksei.tasklist.dto.nulab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskPublishParamsDto {

    private Boolean publishInNulab;
    private Long projectId;
    private Long issueTypeId;
    private Long priorityId;

}
