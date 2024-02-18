package com.example.eatgoodliveproject.serviceimpl;

import com.example.eatgoodliveproject.model.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailSenderService {
    private final JavaMailSender mailSender;

    @Autowired
    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSimpleEmail(String toEmail, String body, String subject){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("anoruehappiness@gmail.com");
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);

        mailSender.send(message);
    }


    public String passwordResetTokenMail(String user, String applicationUrl, String token) {
        String url = applicationUrl + "/user/savePassword?token=" + token;
        //gmail java email send url to user email to reset password/////////////////////////////////
        this.sendSimpleEmail(
                user,
                "Click on your Password link to reset your Password: " + url,
                "Password Reset Link Sent");
        //log url in console to see what was sent to user email
        log.info("Click link to reset your password: {}", url);
        return url;
    }
}
