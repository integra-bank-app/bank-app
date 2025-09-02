package clf.integra.backend.model;
import lombok.*;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
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
}
