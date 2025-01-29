package orkhoian.aleksei.tasklist.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import orkhoian.aleksei.tasklist.dto.validation.OnCreate;
import orkhoian.aleksei.tasklist.dto.validation.OnUpdate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotNull(groups = OnUpdate.class)
    private Long id;
    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    @Length(max = 255)
    private String name;
    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    @Length(max = 255)
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    private String password;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(groups = OnCreate.class)
    private String passwordConfirmation;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String apiKey;
}
