package clf.integra.backend.model;

import clf.integra.backend.audit.AuditListener;
import clf.integra.backend.audit.AuditableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@EntityListeners({AuditListener.class, AuditingEntityListener.class})
@Table(name = "investment")
public class Investment implements AuditableEntity {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Setter
    @Column(nullable = false)
    private int risk;

    @Setter
    @Column(nullable = false)
    private Double balance;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "operation")
    private String operation;

    @Column(name = "timestamp")
    private long timestamp;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

    @Column(name = "modified_by")
    @LastModifiedBy
    private String modifiedBy;

    @Override
    public void audit(String operation) {
        setOperation(operation);
        setTimestamp(new Date().getTime());
    }
}
