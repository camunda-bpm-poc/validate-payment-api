package com.poc.camunda.controller;

import lombok.Data;

@Data
public class PaymentInfo {
    private String ssn;
    private String accountNumber;
    private String paymentAmount;
    private String paymentDate;
}
