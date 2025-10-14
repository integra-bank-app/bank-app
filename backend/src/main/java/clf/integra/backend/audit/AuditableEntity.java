package clf.integra.backend.audit;

public interface AuditableEntity {
    void audit(String operation);
    void setOperation(String operation);
    void setTimestamp(long timestamp);
}
