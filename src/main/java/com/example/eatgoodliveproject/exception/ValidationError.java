package com.example.eatgoodliveproject.exception;

public record ValidationError(
        String field,
        String message
) {
}