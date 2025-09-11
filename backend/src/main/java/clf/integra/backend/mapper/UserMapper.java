package clf.integra.backend.mapper;

import clf.integra.backend.dto.UserDTO;
import clf.integra.backend.model.User;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        if (user == null) return null;

        return new UserDTO(
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastName()
        );
    }
}