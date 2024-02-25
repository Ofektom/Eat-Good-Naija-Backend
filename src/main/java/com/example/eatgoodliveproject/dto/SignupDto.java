package com.example.eatgoodliveproject.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupDto {
    @Size(min = 4, message = "*Enter at least 4 characters")
    private String fullName;
    @Email(message = "*Entry must be an email address")
    @NotEmpty(message = "*Enter your valid email address")
    private String username;
    @NotNull
    @NotEmpty
    private String phoneNumber;
    @Pattern(regexp = "^.*(?=.{8,})(?=...*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "*Enter at least one uppercase,lowercase,digit and special character and minimum 8 characters")
    private String password;
    @Pattern(regexp = "^.*(?=.{8,})(?=...*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "*Enter at least one uppercase,lowercase,digit and special character and minimum 8 characters")
    private String confirmPassword;
}
