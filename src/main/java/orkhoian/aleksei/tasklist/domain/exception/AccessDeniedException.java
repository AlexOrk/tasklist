package orkhoian.aleksei.tasklist.domain.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super();
    }
}
