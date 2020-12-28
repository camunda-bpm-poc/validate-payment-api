package com.poc.camunda.controller;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class CamundaStartService {

    @Autowired
    RuntimeService runtimeService;
    public  void startProcessByMessage(String ssn, String accountNumber, String paymentAmount,String paymentDate){

        runtimeService.createMessageCorrelation("start_process")
                .setVariable("ssn",ssn)
                .setVariable("accountNumber",accountNumber)
                .setVariable("paymentAmount",paymentAmount)
                .setVariable("paymentDate",paymentDate)
                .correlate();
    }

    public  void resumeProcess(String correlationID, String isValid){
        System.out.println("CorrelationID = "+correlationID+",isValid = "+isValid);
        runtimeService.createMessageCorrelation(correlationID)
                .setVariable("isValid",isValid)
                .correlate();
    }

}
