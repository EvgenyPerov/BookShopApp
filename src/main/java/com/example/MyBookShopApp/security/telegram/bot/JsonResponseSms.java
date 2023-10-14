package com.example.MyBookShopApp.security.telegram.bot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonResponseSms {

    @JsonProperty("status")
    private String status;

    @JsonProperty("balance")
    private Double balance;

    @JsonProperty("code")
    private String code;

    @JsonProperty("call_id")
    private String callId;

    @JsonProperty("cost")
    private Double cost;

}
