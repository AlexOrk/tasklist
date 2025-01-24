package orkhoian.aleksei.tasklist.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import orkhoian.aleksei.tasklist.dto.validation.OnCreate;
import orkhoian.aleksei.tasklist.dto.validation.OnUpdate;

@Data
public class UserDto {

    @NotNull(groups = OnUpdate.class)
    private Long id;
    @NotNull(groups = {OnCreate.class, OnUpdate.class})
    @Length(max = 255)
    private String name;
    @NotNull(groups = {OnCreate.class, OnUpdate.class})
    @Length(max = 255)
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(groups = {OnCreate.class, OnUpdate.class})
    private String password;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(groups = OnCreate.class)
    private String passwordConfirmation;
}
