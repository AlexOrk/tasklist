package orkhoian.aleksei.tasklist.domain.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NulabResponseException extends RuntimeException {
    public NulabResponseException(String message) {
        super(message);
    }
}
