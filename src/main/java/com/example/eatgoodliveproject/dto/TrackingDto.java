package com.example.eatgoodliveproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackingDto {
    private boolean isReceived;
    private boolean isPrepared;
    private boolean isReady;
    private boolean inTransit;
    private boolean isDelivered;
}
