package clf.integra.backend.mapper;

import clf.integra.backend.dto.DepositsExportDTO;
import clf.integra.backend.dto.UserDTO;
import clf.integra.backend.model.Deposits;
import clf.integra.backend.model.User;

public class DepositsExportMapper {
    public static DepositsExportDTO toDTO(Deposits deposits) {
        if (deposits == null) {
            return null;
        }

        User user = deposits.getUser();

        if (user == null) {
            return null;
        }

        return DepositsExportDTO.builder()
                .id(deposits.getId())
                .interest_rate(deposits.getInterest_rate())
                .amount(deposits.getAmount())
                .user_id(user.getId())
                .userDTO(UserDTO.
                        builder()
                        .firstName(user.getFirstName())
                        .middleName(user.getMiddleName())
                        .lastName(user.getLastName())
                        .build())
                .build();
    }
}
