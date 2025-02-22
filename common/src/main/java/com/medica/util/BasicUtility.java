package com.medica.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.medica.dto.AppointmentRequest;
import com.medica.dto.DoctorResponse;
import com.medica.dto.NotificationResponse;
import lombok.extern.slf4j.Slf4j;


import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public static String toDate(Date appointmentDate) {
        if (appointmentDate == null) return "";
        LocalDateTime localDateTime = appointmentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static <T> T deserializeJson(List<String> values, int index, Class<T> clazz, ObjectMapper om) {
        if (clazz == null || om == null) {
            throw new IllegalArgumentException("Class type and ObjectMapper cannot be null");
        }

        String json = (index < values.size()) ? values.get(index) : null;

        try {
            return (json != null) ? om.readValue(json, clazz) : createNewInstance(clazz);
        } catch (Exception e) {
            log.error("Deserialization failed for index {}", index, e);
            return createNewInstance(clazz);
        }
    }

    private static <T> T createNewInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Could not create an instance of " + clazz.getSimpleName(), e);
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
