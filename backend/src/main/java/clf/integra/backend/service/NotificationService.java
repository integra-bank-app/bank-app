package clf.integra.backend.service;

import clf.integra.backend.handler.NotificationHandler;
import clf.integra.backend.model.Notification;
import clf.integra.backend.model.NotificationType;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.NotificationRepository;
import clf.integra.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private final NotificationHandler notificationHandler;

    @Transactional
    public void sendNotificationToUser(NotificationType notificationType, String message, UUID userId) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        Notification notification = Notification.builder()
                .user(user)
                .type(notificationType)
                .message(message)
                .build();
        user.getNotifications().add(notification);
        userRepository.save(user);
        notificationHandler.sendNotification(notification);
    }

}
