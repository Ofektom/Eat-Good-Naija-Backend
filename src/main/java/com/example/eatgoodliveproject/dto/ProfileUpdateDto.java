package com.example.eatgoodliveproject.dto;

import com.example.eatgoodliveproject.enums.CountryCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateDto {
    private String fullName;
    private String username;
    private String profilePictureUrl;
    private String phoneNumber;
    private String address;
    private String city;
    private String country;

}
