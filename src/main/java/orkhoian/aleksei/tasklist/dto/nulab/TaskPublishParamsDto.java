package orkhoian.aleksei.tasklist.dto.nulab;

import lombok.Data;

@Data
public class TaskPublishParamsDto {

    private Boolean publishInNulab;
    private Long projectId;
    private Long issueTypeId;
    private Long priorityId;

}
