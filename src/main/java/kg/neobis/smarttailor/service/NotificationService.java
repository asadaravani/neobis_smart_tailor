package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.dtos.NotificationDto;
import kg.neobis.smarttailor.dtos.NotificationPdfDto;

public interface NotificationService {
    void sendNotification(NotificationDto dto1, NotificationPdfDto dto2);

    void sendNotification(NotificationDto dto1);
}
