package kg.neobis.smarttailor.service.impl;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import kg.neobis.smarttailor.dtos.FirebaseNotificationRequest;
import kg.neobis.smarttailor.service.FCMService;
import org.springframework.stereotype.Service;

@Service
public class FCMServiceImpl implements FCMService {
    @Override
    public void sendMessageToToken(FirebaseNotificationRequest request)  {


        Notification notification = Notification.builder()
                .setTitle(request.title())
                .setBody(request.body())
                .build();



        AndroidConfig androidConfig = AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH)
                .build();


        ApnsConfig apnsConfig = ApnsConfig.builder()
                .setAps(Aps.builder()
                        .setSound("default")
                        .setBadge(1)
                        .build())
                .build();

        Message message = Message.builder()
                .setToken(request.token())
                .setNotification(notification)
                .setAndroidConfig(androidConfig)
                .setApnsConfig(apnsConfig)
                .build();


        try {
                String response = FirebaseMessaging.getInstance().send(message);
                System.out.println("Successfully sent message: " + response);
            } catch (Exception e) {
                System.out.println(e.getMessage());;
            }
        }

}
