package clf.integra.backend.service;

import clf.integra.backend.model.Branch;
import clf.integra.backend.model.Notification;
import clf.integra.backend.model.NotificationType;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.NotificationRepository;
import clf.integra.backend.repository.UserRepository;
import clf.integra.backend.handler.NotificationHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationHandler notificationHandler;

    @InjectMocks
    private NotificationService notificationService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        Branch branch = Branch.builder().id(UUID.randomUUID()).build();

        testUser = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .branch(branch)
                .build();
    }

    @Test
    void sendNotificationToUser_userExists_savesNotificationAndCallsHandler() throws IOException {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        notificationService.sendNotificationToUser(NotificationType.INFO, "Hello Mockito!", userId);

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(userRepository, times(1)).save(testUser);

        verify(notificationHandler, times(1)).sendNotification(any(Notification.class));
    }

    @Test
    void sendNotificationToUser_userNotFound_throwsException() throws IOException {
        UUID unknownUserId = UUID.randomUUID();
        when(userRepository.findById(unknownUserId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                notificationService.sendNotificationToUser(NotificationType.INFO, "Hello!", unknownUserId)
        );

        verify(notificationRepository, never()).save(any());
        verify(notificationHandler, never()).sendNotification(any());
    }
}
