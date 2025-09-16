package clf.integra.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid transaction type")
public class InvalidTransactionType extends RuntimeException {
    public InvalidTransactionType(String message) {
        super(message);
    }
}
