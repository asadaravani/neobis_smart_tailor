package kg.neobis.smarttailor.service.impl;

import kg.neobis.smarttailor.dtos.NotificationDto;
import kg.neobis.smarttailor.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate template;
    @Override
    public void sendNotification(NotificationDto message) {
        template.convertAndSend("/topic/notifications", message);
    }
}
