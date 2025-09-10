package clf.integra.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.PAYMENT_REQUIRED)
public class BalanceUpdateFailedException extends RuntimeException {
    public BalanceUpdateFailedException(String message) {
        super(message);
    }
}
