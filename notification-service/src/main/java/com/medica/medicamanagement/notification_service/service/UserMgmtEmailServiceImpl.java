package com.medica.medicamanagement.notification_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medica.exception.InternalServerErrorException;
import com.medica.medicamanagement.notification_service.util.EmailContentResolver;
import com.medica.medicamanagement.notification_service.util.EmailSubjectResolver;
import com.medica.medicamanagement.notification_service.util.EmailTemplateModelUtility;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The type User mgmt email service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserMgmtEmailServiceImpl implements UserMgmtEmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final ObjectMapper om;

    @Override
    public void sendEmailRegardingPasswordChange(String response) {
        try {
            List<String> combinedValues = Arrays.asList(response.split(" <> "));

            String emailId = combinedValues.get(0);
            String name = combinedValues.get(1);
            String subject = EmailSubjectResolver.getSubjectForPasswordChange();

            Map<String, Object> templateModel = EmailTemplateModelUtility.createTemplateModelForPasswordChange(name);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            Context context = new Context();
            context.setVariables(templateModel);

            String htmlBody = EmailContentResolver.getHtmlBodyForPasswordChange(templateEngine, "password-change", context);
            helper.setTo(emailId);

            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
