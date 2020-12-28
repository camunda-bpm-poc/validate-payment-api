package com.poc.camunda.controller;

import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.poc.camunda.controller.CamundaStartService;
import org.json.*;
@RestController

@RequestMapping("/poc")
public class TestController {
    @Autowired
    CamundaStartService camundaStartService;
    @GetMapping("/test")
    public String testMethod(){
        RuntimeService runtimeService = null;
        //ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("my-project-process");
        return "test";
    }

    @RequestMapping(value = "/startProcess", method = RequestMethod.POST)
    public void startProcess(@RequestBody PaymentRequest paymentRequest){
//        String jsonStr = paymentInfo;
//        JSONObject obj = new JSONObject(jsonStr);
//        System.out.println("paymentInfo:"+paymentInfo);

        String ssn = paymentRequest.getPaymentInfo().getSsn();
        String accountNumber = paymentRequest.getPaymentInfo().getAccountNumber();
        String paymentAmount = paymentRequest.getPaymentInfo().getPaymentAmount();
        String paymentDate = paymentRequest.getPaymentInfo().getPaymentDate();
        System.out.println(ssn+" "+accountNumber+" "+paymentAmount+" "+paymentDate);
        camundaStartService.startProcessByMessage(ssn,accountNumber, paymentAmount, paymentDate);
    }

    @RequestMapping(value = "/isPaymentValid/{paymentAmount}", method = RequestMethod.GET)
    public String isPaymentValid(@PathVariable String paymentAmount){
        Integer paymentAmt = Integer.parseInt(paymentAmount);
        if(paymentAmt == 100)
                return "true";
        else
                return "false";
    }
    @RequestMapping(value = "/sentPaymentValidStatus", method = RequestMethod.POST)
    public void resumeProcess(@RequestBody String paymentStatus){
        String jsonStr = paymentStatus;
        JSONObject obj = new JSONObject(jsonStr);
        String correlationID = obj.getJSONObject("paymentStatus").getString("correlationID");
        String isValid = obj.getJSONObject("paymentStatus").getString("isValid");
        camundaStartService.resumeProcess(correlationID,isValid);
    }

}
