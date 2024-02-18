package com.example.eatgoodliveproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupDto {
    private String fullName;
    private String username;
    private String phoneNumber;
    private String password;
    private String confirmPassword;
}
