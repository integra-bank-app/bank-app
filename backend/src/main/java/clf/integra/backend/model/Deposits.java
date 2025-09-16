package clf.integra.backend.model;

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

import java.util.UUID;

@Getter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
