package orkhoian.aleksei.tasklist.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import orkhoian.aleksei.tasklist.domain.task.TaskImage;
import orkhoian.aleksei.tasklist.dto.task.TaskImageDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskImageMapper extends Mappable<TaskImage, TaskImageDto> {
}
