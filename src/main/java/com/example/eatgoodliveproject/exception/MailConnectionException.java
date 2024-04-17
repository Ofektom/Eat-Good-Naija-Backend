package com.example.eatgoodliveproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class MailConnectionException extends Exception{
    public MailConnectionException(String message) {
        super(message);
    }
}
