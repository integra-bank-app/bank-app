package clf.integra.backend.handler;

import clf.integra.backend.mapper.NotificationMapper;
import clf.integra.backend.model.Notification;
import clf.integra.backend.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class NotificationHandler extends TextWebSocketHandler {

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final UserRepository userRepository;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        String uuid = getUUIDFromQuery(session);
        if (uuid == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        UUID userUuid;
        try {
            userUuid = UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        if (userRepository.findById(userUuid).isEmpty()) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        sessions.put(uuid, session);
//        System.out.println("Connected to notifications " + uuid);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        sessions.values().remove(session);
    }

    private String getUUIDFromQuery(WebSocketSession session) {
        if (session.getUri() == null) {
            return null;
        }
        String query = session.getUri().getQuery();
        if (query != null && query.startsWith("uuid=")) {
            return query.substring(5);
        }
        return null;
    }

    public void sendNotification(Notification notification) throws IOException {
        WebSocketSession session = sessions.get(notification.getUser().getId().toString());
        if (session != null && session.isOpen()) {
            String json = NotificationMapper.toJson(notification);
            session.sendMessage(new TextMessage(json));
        }
    }
}
