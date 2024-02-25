package com.example.eatgoodliveproject.enums;

import lombok.Getter;

@Getter
public enum Category {
     RAW("Raw"),
     SOUPS_STEWS("Soups and stews"),
     RICE_DISHES("Rice dishes"),
     GRILLED_ROASTED("Grilled and roasted");
     private final String value;
     Category(String value) {
          this.value = value;
     }

}
