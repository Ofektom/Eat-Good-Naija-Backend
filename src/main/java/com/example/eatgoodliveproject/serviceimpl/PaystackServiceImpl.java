package com.example.eatgoodliveproject.serviceimpl;

import com.example.eatgoodliveproject.dto.paystackDtos.CreatePlanDto;
import com.example.eatgoodliveproject.dto.paystackDtos.InitializePaymentDto;
import com.example.eatgoodliveproject.dto.paystackDtos.PaymentVerificationDto;
import com.example.eatgoodliveproject.enums.PricingPlanType;
import com.example.eatgoodliveproject.model.Users;
import com.example.eatgoodliveproject.model.PaymentPaystack;
import com.example.eatgoodliveproject.dto.paystackResponse.CreatePlanResponse;
import com.example.eatgoodliveproject.dto.paystackResponse.InitializePaymentResponse;
import com.example.eatgoodliveproject.dto.paystackResponse.PaymentVerificationResponse;
import com.example.eatgoodliveproject.repositories.PaystackPaymentRepository;
import com.example.eatgoodliveproject.repositories.UserRepository;
import com.example.eatgoodliveproject.service.PaystackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.eatgoodliveproject.config.AppConstants.*;

@Service
public class PaystackServiceImpl implements PaystackService {

    private final PaystackPaymentRepository paystackPaymentRepository;
    private final UserRepository userRepository;

    @Value("${applyforme.paystack.secret.key}")
    private String paystackSecretKey;

    @Autowired
    public PaystackServiceImpl(PaystackPaymentRepository paystackPaymentRepository, UserRepository userRepository) {
        this.paystackPaymentRepository = paystackPaymentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CreatePlanResponse createPlan(CreatePlanDto createPlanDto) {
        CreatePlanResponse createPlanResponse = null;

        try {
            Gson gson = new Gson();
            StringEntity postingString = new StringEntity(gson.toJson(createPlanDto));
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(PAYSTACK_INIT);
            post.setEntity(postingString);
            post.addHeader("Content-type", "application/json");
            post.addHeader("Authorization", "Bearer " + paystackSecretKey);
            StringBuilder result = new StringBuilder();
            HttpResponse response = client.execute(post);

            if (response.getStatusLine().getStatusCode() == STATUS_CODE_CREATED) {

                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
            } else {
                throw new Exception("Paystack is unable to process payment at the moment " +
                        "or something wrong with request");
            }

            ObjectMapper mapper = new ObjectMapper();
            createPlanResponse = mapper.readValue(result.toString(), CreatePlanResponse.class);
        } catch(Throwable ex) {
            ex.printStackTrace();
        }
        return createPlanResponse;
    }

    @Override
    public InitializePaymentResponse initializePayment(InitializePaymentDto initializePaymentDto) {
        InitializePaymentResponse initializePaymentResponse = null;

        try {
            Gson gson = new Gson();
            StringEntity postingString = new StringEntity(gson.toJson(initializePaymentDto));
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(PAYSTACK_INITIALIZE_PAY);
            post.setEntity(postingString);
            post.addHeader("Content-type", "application/json");
            post.addHeader("Authorization", "Bearer " + paystackSecretKey);
            StringBuilder result = new StringBuilder();
            HttpResponse response = client.execute(post);

            if (response.getStatusLine().getStatusCode() == STATUS_CODE_OK) {

                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
            } else {
                throw new Exception("Paystack is unable to initialize payment at the moment");
            }

            ObjectMapper mapper = new ObjectMapper();
            initializePaymentResponse = mapper.readValue(result.toString(), InitializePaymentResponse.class);

//            InitializePaymentResponse initializePaymentResponse1 = new InitializePaymentResponse();
//
//            if (result != null) {
//                initializePaymentResponse1.setMessage(result.toString());       //changed due to possible mapping issues
//            }

        } catch(Throwable ex) {
            ex.printStackTrace();
        }
        return initializePaymentResponse;
    }

    @Override
    @Transactional
    public PaymentVerificationResponse paymentVerification(String reference, String plan, Long id) throws Exception {
        PaymentVerificationResponse paymentVerificationResponse = null;
        PaymentPaystack paymentPaystack = null;

        try{
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(PAYSTACK_VERIFY + reference);
            request.addHeader("Content-type", "application/json");
            request.addHeader("Authorization", "Bearer " + paystackSecretKey);
            StringBuilder result = new StringBuilder();
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() == STATUS_CODE_OK) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line;

                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
            } else {
                throw new Exception("Paystack is unable to verify payment at the moment");
            }

            ObjectMapper mapper = new ObjectMapper();
            paymentVerificationResponse = mapper.readValue(result.toString(), PaymentVerificationResponse.class);

            if (paymentVerificationResponse == null || paymentVerificationResponse.getStatus().equals("false")) {
                throw new Exception("An error");
            } else if (paymentVerificationResponse.getData().getStatus().equals("success")) {

                Users user = userRepository.getById(id);
                PricingPlanType pricingPlanType = PricingPlanType.valueOf(plan.toUpperCase());

                paymentPaystack = PaymentPaystack.builder()
                        .user(user)
                        .reference(paymentVerificationResponse.getData().getReference())
                        .amount(paymentVerificationResponse.getData().getAmount())
                        .gatewayResponse(paymentVerificationResponse.getData().getGatewayResponse())
                        .paidAt(paymentVerificationResponse.getData().getPaidAt())
                        .createdAt(paymentVerificationResponse.getData().getCreatedAt())
                        .channel(paymentVerificationResponse.getData().getChannel())
                        .currency(paymentVerificationResponse.getData().getCurrency())
                        .ipAddress(paymentVerificationResponse.getData().getIpAddress())
                        .pricingPlanType(pricingPlanType)
                        .createdOn(new Date())
                        .build();
            }
        } catch (Exception ex) {
            throw new Exception("Paystack");
        }
        paystackPaymentRepository.save(paymentPaystack);
        return paymentVerificationResponse;
    }

    @Override
    public List<PaymentVerificationDto> getAllPayments() {

        List<PaymentPaystack> paystackPayments = paystackPaymentRepository.findAll();


        return paystackPayments.stream().
                map(paymentPaystack -> PaymentVerificationDto.builder()
                        .amount(paymentPaystack.getAmount())
                        .paidAt(paymentPaystack.getPaidAt())
                        .gatewayResponse(paymentPaystack.getGatewayResponse())
                        .reference(paymentPaystack.getReference())
                        .channel(paymentPaystack.getChannel())
                        .pricingPlanType(paymentPaystack.getGatewayResponse())
                        .currency(paymentPaystack.getCurrency()).build()).collect(Collectors.toList());
    }


}