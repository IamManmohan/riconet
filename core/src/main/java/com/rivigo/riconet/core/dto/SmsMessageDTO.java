package com.rivigo.riconet.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;



@Getter
@AllArgsConstructor
public class SmsMessageDTO {
    private List<String> phoneNumbers;
    private String message;
}