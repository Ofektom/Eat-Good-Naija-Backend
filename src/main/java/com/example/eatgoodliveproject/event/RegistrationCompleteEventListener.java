package com.example.eatgoodliveproject.event;


import com.example.eatgoodliveproject.model.Users;
import com.example.eatgoodliveproject.serviceimpl.EmailSenderService;
import com.example.eatgoodliveproject.serviceimpl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    private final UserServiceImpl userService;

    private final EmailSenderService emailSenderService;
    @Autowired
    public RegistrationCompleteEventListener(UserServiceImpl userService,
                                             EmailSenderService emailSenderService) {
        this.userService = userService;
        this.emailSenderService = emailSenderService;
    }

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {

        Users user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(user, token);

        String url = event.getApplicationUrl() + "/verifyRegistration?token=" + token;

        log.info("Click the link to verify your account: {}", url);
        if (user.getUsername()!=null){
            emailSenderService.sendSimpleEmail(user.getUsername(), "Click on the verification link to verify your account: " + url,"Verification Token Sent");
        }
        else {
            log.error("User's email address is null. Unable to send verification email.");
        }
    }
}
