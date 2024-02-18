package com.example.eatgoodliveproject.service;


import com.example.eatgoodliveproject.dto.paystackDtos.CreatePlanDto;
import com.example.eatgoodliveproject.dto.paystackDtos.InitializePaymentDto;
import com.example.eatgoodliveproject.dto.paystackDtos.PaymentVerificationDto;
import com.example.eatgoodliveproject.dto.paystackResponse.CreatePlanResponse;
import com.example.eatgoodliveproject.dto.paystackResponse.InitializePaymentResponse;
import com.example.eatgoodliveproject.dto.paystackResponse.PaymentVerificationResponse;

import java.util.List;

public interface PaystackService {
    CreatePlanResponse createPlan(CreatePlanDto createPlanDto) throws Exception;
    InitializePaymentResponse initializePayment(InitializePaymentDto initializePaymentDto);
    PaymentVerificationResponse paymentVerification(String reference, String plan, Long id) throws Exception;

    List<PaymentVerificationDto> getAllPayments();
}