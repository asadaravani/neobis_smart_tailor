package kg.neobis.smarttailor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kg.neobis.smarttailor.dtos.NotificationDto;
import kg.neobis.smarttailor.dtos.NotificationPdfDto;

public interface NotificationService {
    void sendNotification(NotificationDto dto1, NotificationPdfDto dto2);

    void sendNotification(NotificationDto dto1);
    void notifyUsers(String message);

}
