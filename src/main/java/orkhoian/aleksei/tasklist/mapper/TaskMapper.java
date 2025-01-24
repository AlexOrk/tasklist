package orkhoian.aleksei.tasklist.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import orkhoian.aleksei.tasklist.domain.task.Task;
import orkhoian.aleksei.tasklist.dto.task.TaskDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper extends Mappable<Task, TaskDto> {
}
