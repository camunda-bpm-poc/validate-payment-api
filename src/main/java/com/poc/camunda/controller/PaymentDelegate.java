package com.poc.camunda.controller;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentDelegate implements JavaDelegate {
    @Autowired
    CamundaStartService camundaStartService;
    @Autowired
    KafkaService kafkaService;
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        PaymentInfo paymentInfo = camundaStartService.getPaymentInfo();
        Integer paymentAmt = Integer.parseInt(paymentInfo.getPaymentAmount());

        if(paymentAmt <= 100)
            paymentInfo.setValidAmount(true);
        else
            paymentInfo.setValidAmount(false);
        if(paymentAmt < 1000) {
            kafkaService.sendMessage(paymentInfo);
        }
    }
}
