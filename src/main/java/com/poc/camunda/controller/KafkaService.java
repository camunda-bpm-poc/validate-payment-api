package com.poc.camunda.controller;



import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import spinjar.com.fasterxml.jackson.core.JsonProcessingException;
import spinjar.com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
public class KafkaService {

    private static final String KAFKA_TOPIC = "ValidatePaymentTopic";
    private static final String KAFKA_GROUP = "group_id";

    @Autowired
    private CamundaStartService camundaStartService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(PaymentInfo account) {
        log.info(String.format("==> Incoming Message ==> %s", account.toString()));
        log.info("*** Convert User Object to JSON String ***");
        ObjectMapper objectMapper = new ObjectMapper();
        String accountJsonObject = null;
        try {
            accountJsonObject = objectMapper.writeValueAsString(account);
        } catch (JsonProcessingException e) {
            log.error("Error when converting User Object to JSON String: {}", e);
        }
        kafkaTemplate.send(KAFKA_TOPIC, KAFKA_GROUP, accountJsonObject);
        log.info(String.format("==> %s message published", accountJsonObject));
    }

    @KafkaListener(topics = "ValidatePaymentTopic", groupId = "group_id")
    public void consume(String accountJsonObject) {
        log.info(String.format("==> Message Received ==> %s", accountJsonObject));
        ObjectMapper objectMapper = new ObjectMapper();
        PaymentInfo account = null;
        try {
             account = objectMapper.readValue(accountJsonObject, PaymentInfo.class);
            log.info("Received Object ==> " + account.toString());
        } catch (JsonProcessingException e) {
            log.error("ERROR: JSON String is not able to convert to User Object");
        }
        String correlationID = "waitForValidPaymentStatus-"+account.getSsn();
        boolean isValid = account.getValidAmount();
        System.out.println("Resuming Process");
        camundaStartService.resumeProcess(correlationID,isValid);
    }

}