package orkhoian.aleksei.tasklist.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import orkhoian.aleksei.tasklist.domain.task.Status;
import orkhoian.aleksei.tasklist.dto.validation.OnUpdate;
import orkhoian.aleksei.tasklist.dto.validation.OnCreate;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    @NotNull(groups = OnUpdate.class)
    private Long id;
    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    @Length(max = 255)
    private String title;
    @Length(max = 255)
    private String description;
    private Status status;
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime expirationDate;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> images;
}
