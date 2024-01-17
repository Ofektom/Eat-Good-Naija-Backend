package com.example.eatgoodliveproject.controller;

import com.example.eatgoodliveproject.model.Users;
import com.example.eatgoodliveproject.serviceimpl.UserServiceImpl;
import com.example.eatgoodliveproject.utils.GoogleJwtUtils;
import com.example.eatgoodliveproject.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.example.eatgoodliveprojectpersonal.dto.SignupDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private UserServiceImpl userService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private GoogleJwtUtils googleJwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @Test
    public void testSignUpUser() {

        SignupDto signupDto = new SignupDto();
        Users mockedUser = new Users();


        when(userService.saveUser(signupDto)).thenReturn(mockedUser);

        ResponseEntity<SignupDto> responseEntity = authController.signUpUser(signupDto);


        verify(userService, times(1)).saveUser(signupDto);


        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(signupDto);
    }
}
