package com.ccnta.app.notification.service;

import com.ccnta.app.notification.entity.Notification;
import com.ccnta.app.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    private final NotificationRepository notificationRepository;

    @Transactional
    public void notify(Set<String> usernames, Notification notification) {
        for (String username : usernames) {
            notification.setRecipient(username);
            notificationRepository.save(notification);
            send(username, notification);
        }
    }

    private void send(String receiver, Notification notification) {
        try {
            String topic = "/user/" + receiver + "/queue/messages";
            log.info("Sending notification to user: {}", receiver);
            messagingTemplate.convertAndSend(topic, notification);
        } catch (Exception e) {
            log.error("Error sending notification to user {}: {}", receiver, e.getMessage());
        }
    }
}