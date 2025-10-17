package clf.integra.backend.audit;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

public class AuditListener {
    @PrePersist
    @PreUpdate
    @PreRemove
    private void beforeAnyOperation(Object entity) {
        if (entity instanceof AuditableEntity auditable) {
            String operation = switch (getOperation()) {
                case "INSERT" -> "INSERT";
                case "UPDATE" -> "UPDATE";
                case "DELETE" -> "DELETE";
                default -> "UNKNOWN";
            };
            auditable.audit(operation);
        }
    }

    private String getOperation() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement el : stackTrace) {
            if (el.getMethodName().contains("persist")) return "INSERT";
            if (el.getMethodName().contains("merge")) return "UPDATE";
            if (el.getMethodName().contains("remove")) return "DELETE";
        }
        return "UNKNOWN";
    }
}
