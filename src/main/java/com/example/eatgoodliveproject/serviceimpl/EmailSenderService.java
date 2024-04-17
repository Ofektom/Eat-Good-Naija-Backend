package com.example.eatgoodliveproject.serviceimpl;

import com.example.eatgoodliveproject.model.Users;
import jakarta.servlet.http.HttpServletRequest;
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
        message.setFrom("javaspringemailclient@gmail.com");
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);

        mailSender.send(message);
    }

    public String applicationUrl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":" +
                request.getServerPort() +
                "/api/v1/auth" +
                request.getContextPath();
    }


    public String passwordResetTokenMail(String user, String applicationUrl, String token) {
        String url = applicationUrl + "/user/savePassword/" + token;
        this.sendSimpleEmail(
                user,
                "Click on your Password link to reset your Password: " + url,
                "Password Reset Link Sent");
        //log url in console to see what was sent to user email
        log.info("Click link to reset your password: {}", url);
        return url;
    }

    public String forgetPasswordResetTokenMail(Users user, String applicationUrl, String token) {
        String url = applicationUrl + "/api/v1/user/savePassword?token=" + token;
        this.sendSimpleEmail(
                user.getUsername(),
                "Enter code into box on your app to reset your Password: " + token + ". Code will Expire in 10 minutes.",
                "Password Reset Code Sent");
        return url;
    }

}
