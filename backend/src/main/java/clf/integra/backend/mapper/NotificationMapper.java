package clf.integra.backend.mapper;

import clf.integra.backend.dto.NotificationDTO;
import clf.integra.backend.model.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NotificationMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static NotificationDTO toDTO(Notification notification) {
        if (notification == null) return null;
        return NotificationDTO.builder()
                .type(notification.getType())
                .message(notification.getMessage())
                .build();
    }

    public static String toJson(Notification notification) throws JsonProcessingException {
        if (notification == null) return null;
        return objectMapper.writeValueAsString(NotificationMapper.toDTO(notification));
    }
}
