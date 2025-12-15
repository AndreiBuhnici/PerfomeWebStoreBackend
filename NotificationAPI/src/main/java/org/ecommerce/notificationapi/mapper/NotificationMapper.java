package org.ecommerce.notificationapi.mapper;

import lombok.RequiredArgsConstructor;
import org.ecommerce.notificationapi.dto.MailRequest;
import org.ecommerce.notificationapi.service.MailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationMapper {
    private final CommonMapper commonMapper;
    private final MailService mailService;

    @Async
    public void sendMailAsync(MailRequest mailRequest) {
        mailService.sendMail(mailRequest.getTo(), mailRequest.getSubject(), mailRequest.getTemplate(), mailRequest.getAttributes());
    }

}
