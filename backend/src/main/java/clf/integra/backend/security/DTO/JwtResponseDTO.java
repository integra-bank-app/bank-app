package clf.integra.backend.security.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class JwtResponseDTO {
    private String token;
    private String type = "Bearer";
    private UUID id;           // ADAUGĂ ID-ul
    private String firstName;  // ADAUGĂ firstName
    private String lastName;   // ADAUGĂ lastName
    private String email;
    private String role;
    private UUID branchId;

    public JwtResponseDTO(String token, UUID id, String firstName, String lastName, String email, String role, UUID branchId) {
        this.token = token;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.branchId = branchId;
    }
}