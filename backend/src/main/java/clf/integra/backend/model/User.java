package clf.integra.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Getter
@AllArgsConstructor
@ToString
public class User {

    @NonNull
    private UUID id;

    @Setter
    private String firstName;

    @Setter
    private String middleName;

    @Setter
    private String lastName;

    @Setter
    private double balance;

    @Setter
    private UUID branchId;

    private List<Deposit> deposits= new ArrayList<>();
}
