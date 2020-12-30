package com.poc.camunda.controller;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class CamundaStartService {
    private PaymentInfo paymentInfo;

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    @Autowired
    RuntimeService runtimeService;
    public  void startProcessByMessage(PaymentRequest paymentRequest){
        this.paymentInfo = paymentRequest.getPaymentInfo();
        runtimeService.createMessageCorrelation("start_process")
                .setVariable("ssn",paymentRequest.getPaymentInfo().getSsn())
                .setVariable("accountNumber",paymentRequest.getPaymentInfo().getAccountNumber())
                .setVariable("paymentAmount",paymentRequest.getPaymentInfo().getPaymentAmount())
                .setVariable("paymentDate",paymentRequest.getPaymentInfo().getPaymentDate())
                .correlate();
    }

    public  void resumeProcess(String correlationID, boolean isValid){
        System.out.println("CorrelationID = "+correlationID+",isValid = "+isValid);
        runtimeService.createMessageCorrelation(correlationID)
                .setVariable("isValid",isValid)
                .correlate();
    }

}
