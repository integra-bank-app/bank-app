package clf.integra.backend.dto;

import clf.integra.backend.model.NotificationType;
import lombok.Builder;

@Builder
public record NotificationDTO(NotificationType type, String message) {
}
