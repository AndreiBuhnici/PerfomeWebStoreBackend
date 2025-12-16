package org.ecommerce.notificationapi.controller;

import lombok.RequiredArgsConstructor;
import org.ecommerce.notificationapi.dto.MailRequest;
import org.ecommerce.notificationapi.mapper.NotificationMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.ecommerce.notificationapi.constants.PathConstants.API_V1_NOTIFICATION;
import static org.ecommerce.notificationapi.constants.PathConstants.MAIL;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_V1_NOTIFICATION)
public class NotificationController {

    private final NotificationMapper notificationMapper;

    @PostMapping(MAIL)
    @PreAuthorize("hasAuthority('NOTIFIER')")
    public ResponseEntity<?> sendMail(@RequestBody @Valid MailRequest mailRequest) {
        System.out.println(mailRequest.getAttributes().get("order"));
        notificationMapper.sendMailAsync(mailRequest);
        return ResponseEntity.accepted().build();
    }
}
