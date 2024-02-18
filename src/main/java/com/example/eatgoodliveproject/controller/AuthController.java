package com.example.eatgoodliveproject.controller;

import com.example.eatgoodliveproject.dto.LoginDto;
import com.example.eatgoodliveproject.dto.PasswordDto;
import com.example.eatgoodliveproject.dto.PasswordResetDto;
import com.example.eatgoodliveproject.dto.SignupDto;
import com.example.eatgoodliveproject.event.RegistrationCompleteEvent;
import com.example.eatgoodliveproject.model.Users;
import com.example.eatgoodliveproject.model.VerificationToken;
import com.example.eatgoodliveproject.serviceimpl.EmailSenderService;
import com.example.eatgoodliveproject.serviceimpl.UserServiceImpl;
import com.example.eatgoodliveproject.utils.GoogleJwtUtils;
import com.example.eatgoodliveproject.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    private UserServiceImpl userService;
    private JwtUtils jwtUtils;
    private GoogleJwtUtils googleJwtUtils;
    private PasswordEncoder passwordEncoder;
    private ApplicationEventPublisher publisher;
    private EmailSenderService emailSenderService;


    @Autowired
    public AuthController(UserServiceImpl userService, JwtUtils jwtUtils, GoogleJwtUtils googleJwtUtils,
                          PasswordEncoder passwordEncoder, ApplicationEventPublisher publisher,EmailSenderService emailSenderService) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.googleJwtUtils = googleJwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.publisher = publisher;
        this.emailSenderService = emailSenderService;
    }

    @GetMapping("/google/{tkn}")
    public ResponseEntity<String> authorizeOauthUser(@PathVariable("tkn") String token){
        return ResponseEntity.ok(googleJwtUtils.googleOauthUserJWT(token));
    }


    @GetMapping("/dashboard")
    public String index(){
        return "Welcome to your account";
    }


    @PostMapping("/vendor-sign-up")
    public ResponseEntity<String> saveUpAdmin(@RequestBody SignupDto signupDto, final HttpServletRequest request){
        Users user = userService.saveAdmin(signupDto);
        publisher.publishEvent(new RegistrationCompleteEvent(user,applicationUrl(request)));
        return new ResponseEntity<>("Signup successful, go to your mail to verify your account", HttpStatus.OK);
    }

    @PostMapping("/customer-sign-up")
    public ResponseEntity<String> signUpUser(@RequestBody SignupDto signupDto, final HttpServletRequest request){
        Users user = userService.saveUser(signupDto);
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return new ResponseEntity<>("Signup successful, go to your mail to verify your account", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto){
        return userService.loginUser(loginDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication, HttpServletRequest request) {
        String result = userService.logoutUser(authentication, request);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/resendVerifyToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest request){
        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
        Users user = verificationToken.getUser();
        resendVerificationTokenMail(user, applicationUrl(request),verificationToken);
        return "Verification link has been sent to your email";
    }

    void resendVerificationTokenMail(Users user, String applicationUrl, VerificationToken verificationToken) {
        String url = applicationUrl + "/verifyRegistration?token=" + verificationToken.getToken();

        // sendVerificationEmail()
        log.info("Click the link to verify your account: {}", url);
    }

    @GetMapping("/verifyRegistration")
    public ResponseEntity<String> verifyRegistration(@RequestParam("token") String token){
        String result = userService.validateVerificationToken(token);
        if (result.equalsIgnoreCase("valid")){
            return new ResponseEntity<>( "User Verified Successfully",HttpStatus.OK);
        }
        return new ResponseEntity<>("Bad User", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetDto passwordResetDto, HttpServletRequest request){
        Users user = userService.findUserByEmail(passwordResetDto.getEmail());
        String url = "";
        if (user != null){
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            url = passwordResetTokenMail(user, applicationUrl(request), token);

        }
        return new ResponseEntity<>("go to your mail to reset your password", HttpStatus.OK);

    }


    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordDto passwordDto){
        Users user = userService.findUserByEmail(passwordDto.getEmail());
        if (!userService.checkIfValidOldPassword(user, passwordDto.getOldPassword())){
            return "Invalid Old Password";
        }


        userService.changePassword(user, passwordDto.getNewPassword());
        return "Password Change Successfully";
    }

    private String passwordResetTokenMail(Users user, String applicationUrl, String token) {
        String url = applicationUrl + "/savePassword?token=" + token;

        emailSenderService.sendSimpleEmail(
                user.getUsername(),
                "Click the link to reset your password: " + url,
                "Password Reset sent"
        );

        log.info("Click the link to Reset your password: {}", url);
        return url;

    }

    @PostMapping("/savePassword")
    public ResponseEntity<String> savePassword(@RequestParam("token") String token, @RequestBody PasswordDto passwordDto){
        String result = userService.validatePasswordResetToken(token);
        if (!result.equalsIgnoreCase("valid")){
            return new ResponseEntity<>("Invalid Token", HttpStatus.NOT_FOUND);
        }
        Optional<Users> user = userService.getUserByPasswordResetToken(token);
        if (user.isPresent()){
            userService.changePassword(user.get(), passwordDto.getNewPassword());
            return new ResponseEntity<>("Password Reset Successfully", HttpStatus.OK);
        }else {
            return new ResponseEntity<>("Invalid Token", HttpStatus.BAD_REQUEST);
        }
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + "/api/v1/auth" + request.getContextPath();
    }



}
