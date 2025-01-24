package orkhoian.aleksei.tasklist.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import orkhoian.aleksei.tasklist.domain.user.User;
import orkhoian.aleksei.tasklist.dto.user.UserDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper extends Mappable<User, UserDto> {
}
