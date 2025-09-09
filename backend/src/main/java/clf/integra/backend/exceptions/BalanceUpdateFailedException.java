package clf.integra.backend.exceptions;

public class BalanceUpdateFailedException extends RuntimeException {
    public BalanceUpdateFailedException(String message) {
        super(message);
    }
}
