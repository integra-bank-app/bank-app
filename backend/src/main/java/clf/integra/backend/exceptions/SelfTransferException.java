package clf.integra.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Self transfer not allowed")
public class SelfTransferException extends RuntimeException {
    public SelfTransferException(String message) {
        super(message);
    }
}