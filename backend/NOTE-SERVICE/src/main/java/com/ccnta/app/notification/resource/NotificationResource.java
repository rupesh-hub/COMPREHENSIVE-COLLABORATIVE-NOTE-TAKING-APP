package com.ccnta.app.notification.resource;

import com.ccnta.app.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class NotificationResource {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/application") //mapped as: /ccnta/application
    @SendTo("/broadcast/messages")
    public Notification notifyAll(final Notification notification) {
        return notification;
    }

    @MessageMapping("/private")
    public void notifyUser(@Payload Notification notification){
        messagingTemplate.convertAndSendToUser(notification.getRecipient(), "/queue/notifications", notification);
    }

}
