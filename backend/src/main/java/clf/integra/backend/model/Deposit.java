package clf.integra.backend.model;

import lombok.*;

import java.util.UUID;

@Getter
@ToString
public class Deposit {

    @NonNull
    private UUID id;

    @Setter
    private Double interest_rate;

    @Setter
    private Double amount;

    public Deposit(UUID id, Double interest_rate,Double amount) {
        this.id = id;
        this.interest_rate = interest_rate;
        this.amount = amount;
    }
}
