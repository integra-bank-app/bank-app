package clf.integra.backend.service;

import clf.integra.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Double getBalance(UUID id) {
        try {
            return userRepository.getBalance(id);
        }catch (NullPointerException e){
            return null;
        }
    }
}
