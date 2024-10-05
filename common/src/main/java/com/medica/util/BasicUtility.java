package com.medica.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.medica.dto.AppointmentRequest;
import com.medica.dto.DoctorResponse;
import com.medica.dto.NotificationResponse;
import lombok.extern.slf4j.Slf4j;


import java.net.URL;

@Slf4j
public final class BasicUtility {
    private static final ObjectMapper om = new ObjectMapper();
    private BasicUtility() {
        throw new UnsupportedOperationException("Not supported");
    }


    /**
     * Read specific property string.
     *
     * @param body the body
     * @param prop the prop
     * @return the string
     */
    public static String readSpecificProperty(String body, String prop) {
        try {
            if (body == null || body.isEmpty()) {
                log.error("Provided JSON string is null or empty");
                return "";
            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.readTree(body);

            return JsonPath.read(body, prop);
        } catch (Exception e) {
            log.error("Error reading property: {} from body: {}", prop, body, e);
            return "";
        }
    }


    public static String getDomainFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            return url.getHost();
        } catch (Exception e) {
            return null;
        }
    }

    public static NotificationResponse generateNotificationResponse(String message, String status) {
        return NotificationResponse.builder().uid(DefaultValuesPopulator.getUid())
                .timestamp(DefaultValuesPopulator.getCurrentTimestamp())
                .status(status).message(message)
                .build();
    }

    public static String stringifyObject(Object obj) {
        try {
            return om.writeValueAsString(obj);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
