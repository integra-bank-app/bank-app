package clf.integra.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Deposits {

    @Id
    @GeneratedValue
    private UUID id;

    @Setter
    private Double interest_rate;

    @Setter
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
