package clf.integra.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class FeeTaxTransaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    private double amount;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // foreign key column in fees
    private User user;

    @Setter
    @CreationTimestamp
    private LocalDateTime createdAt;

    public FeeTaxTransaction(User user, double amount) {
        this.user = user;
        this.amount = amount;
    }

}
