package clf.integra.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
public class FeeTaxTransaction implements Serializable {

    @Id
    @GeneratedValue
    @Generated(GenerationTime.INSERT)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Setter
    private double amount;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // foreign key column in fees
    private User user;

    @Setter
    @CreationTimestamp
    private LocalDateTime createdAt;

}
