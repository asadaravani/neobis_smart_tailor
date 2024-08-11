package kg.neobis.smarttailor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import kg.neobis.smarttailor.config.NotificationWebSocketHandler;
import kg.neobis.smarttailor.dtos.NotificationDto;
import kg.neobis.smarttailor.dtos.NotificationPdfDto;
import kg.neobis.smarttailor.exception.InvalidJsonException;
import kg.neobis.smarttailor.exception.InvalidRequestException;
import kg.neobis.smarttailor.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final ObjectMapper objectMapper;
    private final ConnectionFactory connectionFactory;
    private final NotificationWebSocketHandler notificationWebSocketHandler;

    private final static String QUEUE_PDF = "pdf";
    @Override
    public void sendNotification(NotificationDto message, NotificationPdfDto pdf) {

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {

            String notificationJson = convertToJson(message);
            notifyUsers( notificationJson);

            channel.queueDeclare(QUEUE_PDF, false, false, false, null);
            channel.basicPublish("", QUEUE_PDF, null, convertToByte(pdf));

        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendNotification(NotificationDto message) throws JsonProcessingException {
        String notificationJson = convertToJson(message);
            notifyUsers( notificationJson);
        }

    @Override
    public void notifyUsers(String message) {
        try {
            notificationWebSocketHandler.sendNotification(message);
        } catch (Exception e) {
            throw new InvalidRequestException("Invalid notification exception");
        }

    }

    private byte[] convertToByte(Object dto)  {
        byte [] result;
        try {
          result =   objectMapper.writeValueAsBytes(dto);
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(e.getMessage());
        }
         return result;
    }

    private String convertToJson(NotificationDto notificationDto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(notificationDto);
    }
}
