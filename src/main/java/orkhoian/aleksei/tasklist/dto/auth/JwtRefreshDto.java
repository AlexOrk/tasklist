package orkhoian.aleksei.tasklist.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtRefreshDto {

    @NotBlank
    private String refreshToken;
}
