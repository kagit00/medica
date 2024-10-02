package com.medica.medicamanagement.patient_service.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SMSService {

    @Autowired
    private Environment env;


    @PostConstruct
    public void initTwilio() {
        Twilio.init(
                Objects.requireNonNull(env.getProperty("twilio.account.sid")),
                Objects.requireNonNull(env.getProperty("twilio.auth.token"))
        );
    }

    public void sendSms(String toPhoneNumber, String messageBody) {
        Message.creator(
                        new PhoneNumber(toPhoneNumber),
                        new PhoneNumber(env.getProperty("twilio.from.phone.number")),
                        messageBody)
                .create();
    }
}
