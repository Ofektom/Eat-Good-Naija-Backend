package com.example.eatgoodliveproject.model;



import com.example.eatgoodliveproject.enums.ChatStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String senderName;
    private String receiverName;
    private String message;
    private String date;
    private ChatStatus status;

}
