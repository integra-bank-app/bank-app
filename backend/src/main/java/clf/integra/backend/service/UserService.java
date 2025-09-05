package clf.integra.backend.service;

import clf.integra.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Double getUserBalanceById(UUID id) {
        return userRepository.getUserBalanceById(id);

    }
}
