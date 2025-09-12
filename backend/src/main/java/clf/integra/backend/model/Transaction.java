package clf.integra.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Setter
    @Column(name = "amount", nullable = false)
    private double amount;

    @Setter
    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Setter
    @Column(name = "timestamp", nullable = false)
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @Setter
    @Column(name = "description")
    private String description;

    @Setter
    @Column(name = "reference_transaction_id")
    private UUID referenceTransactionId;
}
