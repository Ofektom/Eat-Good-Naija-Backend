package com.example.eatgoodliveproject.utils;

import java.util.UUID;

public class UniquePaymentIdGenerator {
    public static String generateId(){
        UUID id=UUID.randomUUID();
        return id.toString();
    }
}
